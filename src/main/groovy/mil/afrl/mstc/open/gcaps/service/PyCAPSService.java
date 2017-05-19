/*
 * Copyright to the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mil.afrl.mstc.open.gcaps.service;

import mil.afrl.mstc.open.gcaps.PyCAPS;
import mil.afrl.mstc.open.gcaps.PyCAPSException;
import mil.afrl.mstc.open.gcaps.PyCAPSFactory;
import mil.afrl.mstc.open.gcaps.PyCAPSManager;
import mil.afrl.mstc.open.gcaps.entry.PyCapsEntry;
import mil.afrl.mstc.open.gcaps.proxy.PyCAPSProxy;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;
import org.rioproject.annotation.CreateProxy;
import org.rioproject.annotation.PreAdvertise;
import org.rioproject.impl.associations.AssociationProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Dennis Reedy
 */
@SuppressWarnings("unused")
public class PyCAPSService implements PyCAPSFactory {
    private JavaSpace space;
    private TransactionManager tranMgr;
    private PyCAPSManager pyCAPSManager = new PyCAPSManager();
    private CountDownLatch serviceListener = new CountDownLatch(1);
    private boolean shutdown = false;
    private ExecutorService service;
    private static final Logger logger = LoggerFactory.getLogger(PyCAPSService.class);

    public PyCAPSService() {

    }

    public void setParameters(Map<String, Object> parms) {
        String nativeDist = (String)parms.get("native.dist");
        System.setProperty("native.open.dist", nativeDist);
        new Thread(() -> pyCAPSManager.launch(() -> serviceListener.countDown())).start();
    }

    public void setJavaSpace(JavaSpace space) {
        this.space = space;
    }

    public void setTransactionManager(TransactionManager tranMgr) {
        this.tranMgr = AssociationProxyUtil.getService(tranMgr);
    }

    @PreDestroy
    public void shutdown() {
        shutdown = true;
        pyCAPSManager.destroy();
        if (service != null)
            service.shutdownNow();
    }

    @CreateProxy
    public PyCAPSProxy createProxy(PyCAPSFactory exported) {
        logger.info("Create PyCAPSProxy, make sure service is up...");
        try {
            serviceListener.await();
        } catch (InterruptedException e) {
            logger.debug("CountdownLatch interrupted", e);
        }
        logger.info("Create PyCAPSProxy {}, {}", pyCAPSManager.getAddress(), pyCAPSManager.getPort());
        return PyCAPSProxy.create(pyCAPSManager.getAddress(), pyCAPSManager.getPort(), exported);
    }

    @PreAdvertise
    public void startup() {
        if(space!=null) {
            int workerTasks = 1;
            service = Executors.newFixedThreadPool(workerTasks);
            service.submit(new SpaceProcessor(new PyCapsEntry(PyCapsEntry.State.NEW)));
        }
    }

    @Override public PyCAPS create() throws IOException {
        throw new IOException("Use proxy");
    }

    class SpaceProcessor implements Runnable {
        PyCapsEntry template;

        SpaceProcessor(PyCapsEntry template) {
            this.template = template;
        }

        public void run() {
            long timeout = TimeUnit.SECONDS.toMillis(10);
            PyCAPS pyCAPS = pyCAPSManager.getPyCAPS();
            while (!shutdown) {
                try {
                    Transaction tx = null;
                    if (tranMgr != null) {
                        tx = TransactionFactory.create(tranMgr, Lease.FOREVER).transaction;

                    }
                    PyCapsEntry entry = (PyCapsEntry)space.takeIfExists(template, tx, timeout);
                    if (entry == null && tx != null) {
                        tx.abort();
                    } else {
                        if(entry!=null) {
                            logger.info("Worker processing task: {}", entry);

                            String request = entry.getMessage().getRequest();
                            Map response = null;
                            PyCapsEntry.State state;
                            try {
                                response = (Map)pyCAPS.submit(request);
                                state = PyCapsEntry.State.COMPLETE;
                            } catch (PyCAPSException e) {
                                state = PyCapsEntry.State.ERROR;
                                logger.error("pyCAPS failed", e);
                            }
                            entry.state = state;
                            entry.getMessage().setResponse(response);
                            space.write(entry, tx, timeout);
                            if(tx != null)
                                tx.commit();
                        }
                    }
                } catch (InterruptedException e) {
                    logger.info("SpaceProcessor InterruptedException, exiting");
                    break;

                } catch (Exception e) {
                    logger.warn("Worker processing task", e);
                    break;
                }
            }
        }
    }

}
