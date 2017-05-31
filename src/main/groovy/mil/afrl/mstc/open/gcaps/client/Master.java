package mil.afrl.mstc.open.gcaps.client;

import com.google.gson.Gson;
import mil.afrl.mstc.open.gcaps.entry.Message;
import mil.afrl.mstc.open.gcaps.entry.PyCapsEntry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace05;
import org.rioproject.associations.Association;
import org.rioproject.associations.AssociationDescriptor;
import org.rioproject.associations.AssociationType;
import org.rioproject.deploy.DeployAdmin;
import org.rioproject.impl.associations.DefaultAssociationManagement;
import org.rioproject.impl.opstring.OpStringLoader;
import org.rioproject.impl.util.ThrowableUtil;
import org.rioproject.monitor.ProvisionMonitor;
import org.rioproject.opstring.OperationalString;
import org.rioproject.opstring.OperationalStringException;
import org.rioproject.url.ProtocolRegistryService;
import org.rioproject.url.artifact.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Dennis Reedy
 */
public class Master {
    private JavaSpace05 space;
    private ExecutorService service;
    private Taker completedTaker;
    private Taker errorTaker;
    private PyCapsListener pyCapsListener;
    private List<PyCapsEntry> collectedWork = new ArrayList<>();
    private DefaultAssociationManagement associationManagement;
    static {
        Policy.setPolicy(
            new Policy() {
                public PermissionCollection getPermissions(CodeSource codesource) {
                    Permissions perms = new Permissions();
                    perms.add(new AllPermission());
                    return(perms);
                }
                public void refresh() {
                }

            });
        System.setSecurityManager(new SecurityManager());
        Properties props = load();
        for (Map.Entry<Object, Object> e : props.entrySet()) {
            String key = (String) e.getKey();
            String value = getProperty(key, props);
            if(System.getProperty(key)==null)
                System.setProperty(key, value);
        }
        ProtocolRegistryService.create().register("artifact", new Handler());
    }
    private static final Logger logger = LoggerFactory.getLogger(Master.class);

    private Master(DefaultAssociationManagement associationManagement) throws ExecutionException, InterruptedException {
        this.associationManagement = associationManagement;
        AssociationDescriptor spaceDesc = new AssociationDescriptor(AssociationType.REQUIRES);
        spaceDesc.setInterfaceNames(JavaSpace05.class.getName());
        spaceDesc.setGroups(System.getProperty("user.name"));

        Association<JavaSpace05> spaceAssociation = associationManagement.addAssociationDescriptor(spaceDesc);
        logger.info("Getting JavaSpace...");
        space = spaceAssociation.getServiceFuture().get();
        logger.debug("Obtained javaSpace {}", spaceDesc);
        initialize(space);
    }

    private void initialize(JavaSpace05 space) {
        this.space = space;
        service = Executors.newFixedThreadPool(2);
        pyCapsListener = new PyListener();
        startTakers(pyCapsListener);
    }

    @SuppressWarnings("unchecked")
    private Map submit(String request) throws IOException, TransactionException, InterruptedException {
        PyCapsEntry pyCapsEntry = new PyCapsEntry(PyCapsEntry.State.NEW);
        Message message = new Message(1);
        message.setRequest(request);
        pyCapsEntry.setMessage(message);
        space.write(pyCapsEntry, null, Lease.FOREVER);
        CountDownLatch numToGet = new CountDownLatch(1);
        pyCapsListener.setCountDownLatch(numToGet);
        numToGet.await();
        Map result = new HashMap();
        for(PyCapsEntry entry : collectedWork) {
            result.putAll(entry.getMessage().getResponse());
        }
        return result;
    }

    private void startTakers(PyCapsListener pyCapsListener) {
        PyCapsEntry completeTemplate = new PyCapsEntry(PyCapsEntry.State.COMPLETE);
        PyCapsEntry errorTemplate = new PyCapsEntry(PyCapsEntry.State.ERROR);
        completedTaker = new Taker(completeTemplate, pyCapsListener);
        errorTaker = new Taker(errorTemplate, pyCapsListener);
        service.submit(completedTaker);
        service.submit(errorTaker);
    }

    private void shutdown(DeployAdmin deployAdmin, String deploymentName) throws OperationalStringException, RemoteException {
        completedTaker.shutdown = true;
        errorTaker.shutdown = true;
        service.shutdownNow();
        associationManagement.terminate();
        if(deployAdmin!=null)
            deployAdmin.undeploy(deploymentName);
    }

    public static void main(String[] args) throws Exception {
        if(args.length<2)
            throw new IllegalArgumentException("Invocation of this utility requires 2 arguments, " +
                                               "an input JSON file and the output file to be written");
        String input = args[0];
        String output = args[1];

        StringBuilder toSubmit = new StringBuilder();
        for(String line : Files.readAllLines(new File(input).toPath())) {
            if(toSubmit.length()>0)
                toSubmit.append("\n");
            toSubmit.append(line);
        }

        Gson gson = new Gson();
        URL url = Master.class.getClassLoader().getResource("deploy/pyCapsDeploy.groovy");
        if(url==null)
            throw new RuntimeException("Could not load deploy/pyCapsDeploy.groovy");
        String deploymentName = null;
        Master master = null;
        DeployAdmin deployAdmin = null;
        try {
            OpStringLoader opStringLoader = new OpStringLoader();
            OperationalString deployment = opStringLoader.parseOperationalString(url)[0];
            deploymentName = deployment.getName();
            DefaultAssociationManagement associationManagement = new DefaultAssociationManagement();
            deployAdmin = getDeployAdmin(associationManagement);
            logger.info("Deploying pyCAPS service...");
            deployAdmin.deploy(deployment);
            master = new Master(associationManagement);
            logger.info("Submitting request...");
            Map results  = master.submit(toSubmit.toString());
            File resultsFile = new File(output);
            if (resultsFile.exists()) {
                if(resultsFile.delete())
                    logger.debug("Removed {}", resultsFile.getPath());
            }
            logger.info("Writing result");
            Files.write(resultsFile.toPath(), gson.toJson(results).getBytes());
        } finally {
            if(master!=null)
                master.shutdown(deployAdmin, deploymentName);
        }
    }


    interface PyCapsListener {
        void setCountDownLatch(CountDownLatch countDownLatch);
        void notify(PyCapsEntry pyWork);
    }

    private class Taker implements Runnable {
        private PyCapsEntry template;
        private boolean shutdown;
        private PyCapsListener pyCapsListener;

        Taker(PyCapsEntry template, PyCapsListener pyCapsListener) {
            this.template = template;
            this.pyCapsListener = pyCapsListener;
        }

        @Override public void run() {
            logger.debug("Started Taker for {}", template.state.name());
            while (!shutdown) {
                try {
                    PyCapsEntry result = (PyCapsEntry) space.takeIfExists(template, null, TimeUnit.MINUTES.toMillis(1));
                    if (result == null) continue;
                    if (pyCapsListener != null) pyCapsListener.notify(result);
                    if (result.state.equals(PyCapsEntry.State.ERROR)) {
                        logger.error("Issue running PyCAPS", result.getMessage().getCaughtWhileProcessing());
                    }

                } catch (UnusableEntryException e) {
                    StringBuilder b = new StringBuilder();
                    if (e.unusableFields != null && e.unusableFields.length > 0) {
                        b.append("unusable fields: ").append(e.unusableFields.length);
                        for (String f : e.unusableFields) {
                            b.append("\n\t").append(f);
                        }

                    }
                    logger.error("Error taking {} entries from space\n{}", template.state.name(), b.toString(), e);
                } catch(UnmarshalException e) {
                    /*logger.warn("Taking {} entries from space {}: {}",
                                 template.state.name(), e.getClass().getName(), e.getMessage());*/
                } catch (RemoteException e) {
                    logger.error("Error taking {} entries from space", template.state.name(), e);
                    if(!ThrowableUtil.isRetryable(e))
                        break;
                } catch (Exception e) {
                    logger.error("Error taking {} entries from space", template.state.name(), e);
                }

            }
        }
    }

    private static Properties load() {
        URL url = Master.class.getClassLoader().getResource("systemProps.properties");
        Properties properties = new Properties();
        if(url==null)
            logger.error("Failed loading systemProps.properties");
        else {
            try (InputStream rs = url.openStream()) {
                properties.load(rs);
            } catch (IOException e) {
                logger.error("Failed loading systemProps.properties", e);
            }
        }
        return properties;
    }

    private static String getProperty(String key, Properties properties) {
        String value = properties.getProperty(key);
        return org.rioproject.util.PropertyHelper.expandProperties(value, properties);
    }

    private static DeployAdmin getDeployAdmin(DefaultAssociationManagement associationManagement) throws Exception {
        AssociationDescriptor managerDesc = new AssociationDescriptor(AssociationType.REQUIRES);
        managerDesc.setInterfaceNames(ProvisionMonitor.class.getName());
        managerDesc.setGroups((String[])null);

        Association<ProvisionMonitor> managerAssociation = associationManagement.addAssociationDescriptor(managerDesc);
        logger.info("Getting ProvisionMonitor");
        ProvisionMonitor manager = managerAssociation.getServiceFuture().get();
        return (DeployAdmin) manager.getAdmin();
    }

    private class PyListener implements  PyCapsListener {
        CountDownLatch countDownLatch;
        @Override public void setCountDownLatch(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override public void notify(PyCapsEntry pyWork) {
            logger.info("Received {}", pyWork.state.name());
            collectedWork.add(pyWork);
            countDownLatch.countDown();
        }
    }
}
