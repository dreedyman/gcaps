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
package mil.afrl.mstc.open.gcaps

import mil.afrl.mstc.open.gcaps.client.Client
import mil.afrl.mstc.open.gcaps.proxy.PyCAPSProxy
import org.rioproject.associations.Association
import org.rioproject.associations.AssociationDescriptor
import org.rioproject.associations.AssociationManagement
import org.rioproject.associations.AssociationType
import org.rioproject.config.Constants
import org.rioproject.impl.associations.DefaultAssociationManagement
import org.rioproject.net.HostUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit

/**
 *
 * @author Dennis Reedy
 */
class PyCAPSManager {
    private int port
    private String address
    private Process pythonProcess
    static {
        if(System.getSecurityManager()==null)
            System.setSecurityManager(new SecurityManager())
    }
    static Logger logger = LoggerFactory.getLogger(PyCAPSManager.class)

    int getPort() {
        return port
    }

    String getAddress() {
        if(address==null)
            address = InetAddress.getLocalHost().getHostAddress()
        return address
    }
    def launch() {
        launch(null)
        this
    }

    def launch(LaunchListener listener) {
        address = HostUtil.getHostAddressFromProperty(Constants.RMI_HOST_ADDRESS)
        URL url = PyCAPSManager.class.getClassLoader().getResource("python/pyCAPSService.py")
        File scriptDir = new File(System.getProperty("script.run.dir",
                                                     "/tmp/${System.getProperty("user.name")}/python"))
        if(!scriptDir.exists())
            scriptDir.mkdirs()
        File script = new File(scriptDir, "pyCAPSService.py")
        if(script.exists())
            script.delete()
        script << url.getText()

        File pythonDir = script.getParentFile()
        File portFile = new File(pythonDir, "port.txt")
        if(portFile.exists())
            portFile.delete()
        File nativeLibDist = new File(System.getProperty("native.open.dist")+"/"+OS.get())
        String command = String.format("python %s %s %s", script.getPath(), portFile.getPath(), nativeLibDist.getPath())
        logger.info("Launching {}", command)
        pythonProcess = OS.execBackground(command, pythonDir)
        logger.info("Completed launch, get port ...")

        def portFileCheck = Thread.start {
            try {
                logger.info("Wait for ${portFile.path} to be created")
                while (!portFile.exists()) {
                    Thread.sleep(500)
                }
                logger.info("Wait for ${portFile.path} to be written")
                while (portFile.text.length() == 0) {
                    Thread.sleep(500)
                }
                port = Integer.parseInt(portFile.text)
                logger.info("Port read: ${port}")
            } catch(InterruptedException ignored) {
                logger.info("portFileCheck interrupted")
            }
        }
        def alarm = new Timer()
        alarm.runAfter(5000) {
            portFileCheck.interrupt()
        }
        portFileCheck.join()
        alarm.cancel()
        if(port==0)
            throw new IllegalStateException("Port file ${portFile.path} not created or written to")
        if(listener!=null)
            listener.created()
        this
    }

    PyCAPS discover() {
        logger.info("Create association management to discover PyCAPS")
        AssociationDescriptor pyCAPSDesc = new AssociationDescriptor(AssociationType.REQUIRES)
        pyCAPSDesc.setInterfaceNames(PyCAPSFactory.class.getName())
        pyCAPSDesc.setGroups("dreedy")

        AssociationManagement a = new DefaultAssociationManagement()
        Association<PyCAPSFactory> association = a.addAssociationDescriptor(pyCAPSDesc)
        PyCAPSFactory pyCAPSFactory = association.getServiceFuture().get(5, TimeUnit.SECONDS)
        return pyCAPSFactory.create()
    }

    PyCAPS getPyCAPS() {
        if(port==0)
            return discover()
        return PyCAPSProxy.create(address, port)
    }

    void destroy() {
        logger.info("Shutting down pyCAPS service")
        Client client = new Client(address, port)
        client.shutdown()
    }

    interface LaunchListener {
        void created();
    }
}
