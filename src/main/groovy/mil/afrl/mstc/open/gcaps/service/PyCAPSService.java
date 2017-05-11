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
import mil.afrl.mstc.open.gcaps.PyCAPSFactory;
import mil.afrl.mstc.open.gcaps.PyCAPSManager;
import mil.afrl.mstc.open.gcaps.proxy.PyCAPSProxy;
import org.rioproject.annotation.CreateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Dennis Reedy
 */
public class PyCAPSService implements PyCAPSFactory {
    private PyCAPSManager pyCAPSManager = new PyCAPSManager();
    private CountDownLatch serviceListener = new CountDownLatch(1);
    private static final Logger logger = LoggerFactory.getLogger(PyCAPSService.class);

    public PyCAPSService() {
        new Thread(() -> pyCAPSManager.launch(() -> serviceListener.countDown())).start();
    }

    @PreDestroy
    public void shutdown() {
        pyCAPSManager.destroy();
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

    @Override public PyCAPS create() throws IOException {
        throw new IOException("Use proxy");
    }

}
