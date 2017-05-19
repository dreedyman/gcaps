package mil.afrl.mstc.open.gcaps;

import mil.afrl.mstc.open.gcaps.entry.Message;
import mil.afrl.mstc.open.gcaps.entry.PyCapsEntry;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace05;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rioproject.test.RioTestRunner;
import org.rioproject.test.SetTestManager;
import org.rioproject.test.TestManager;

import java.io.IOException;
import java.util.concurrent.*;

import static org.junit.Assert.assertNotNull;

/**
 * @author Dennis Reedy
 */
@RunWith(RioTestRunner.class)
public class PyCAPSManagerTest {
    @SetTestManager
    static TestManager testManager;

    @Test
    public void submitter() throws IOException, TransactionException, InterruptedException, ExecutionException, TimeoutException {
        JavaSpace05 space = testManager.waitForService(JavaSpace05.class);
        /*File jsonData = new File(System.getProperty("user.dir")+"/src/test/json/mstc-pycaps.json");
        String request = new String(Files.readAllBytes(jsonData.toPath()));*/
        String request = getConfig();
        Message message = new Message(1);
        message.setRequest(request);
        PyCapsEntry pyCapsEntry = new PyCapsEntry(PyCapsEntry.State.NEW);
        pyCapsEntry.setMessage(message);
        space.write(pyCapsEntry, null, Lease.FOREVER);
        PyCapsEntry completeTemplate = new PyCapsEntry(PyCapsEntry.State.COMPLETE);
        PyCapsEntry errorTemplate = new PyCapsEntry(PyCapsEntry.State.ERROR);

        Taker completedTaker = new Taker(completeTemplate, space);
        Taker errorTaker = new Taker(errorTemplate, space);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<PyCapsEntry> completed = executor.submit(completedTaker);
        Future <PyCapsEntry> errors = executor.submit(errorTaker);
        PyCapsEntry complete = completed.get(3, TimeUnit.MINUTES);
        System.out.println("===> "+complete.getMessage().getResponse());
    }

    private String getConfig() {
        AstrosConfig astros = new AstrosConfig(System.getProperty("projectDataRoot"),
                                               System.getProperty("projectDir"),
                                               "AstrosModalAGARD445",
                                               "http://${webster.address}:${webster.port}");
        return (String) astros.get();
    }

    //@Test
    public void launch() throws Exception {
        PyCAPSManager manager = new PyCAPSManager();
        PyCAPS pyCAPS = manager.discover();
        try {
            //manager.launch();
            assertNotNull(pyCAPS);
            /*assertTrue(manager.getPort() != 0);
            assertTrue(manager.getAddress() != null);*/
        } finally {
            pyCAPS.shutdown();
        }
    }

    private class Taker implements Callable<PyCapsEntry> {
        PyCapsEntry template;
        JavaSpace05 space;
        boolean shutdown;

        Taker(PyCapsEntry template, JavaSpace05 space) {
            this.template = template;
            this.space = space;
        }

        @Override public PyCapsEntry call() throws Exception {
            while (!shutdown) {
                try {
                    PyCapsEntry entry = (PyCapsEntry) space.take(template, null, TimeUnit.MINUTES.toMillis(1));
                    if(entry!=null) {
                        System.out.println("===> Received "+entry.state.name());
                        return entry;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}