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
import org.rioproject.config.Constants

/**
 * The deployment configuration for pyCAPS service
 *
 * @author Dennis Reedy
 */
deployment(name:'pyCAPS') {
    groups System.getProperty(Constants.GROUPS_PROPERTY_NAME,
                              System.getProperty('user.name'))

    artifact id: 'outrigger-dl',   'org.apache.river:outrigger-dl:2.2.2'
    artifact id: 'outrigger-impl', 'org.apache.river:outrigger:2.2.2'
    artifact id: 'mahalo-dl',      'org.apache.river:mahalo-dl:2.2.2'
    artifact id: 'mahalo-impl',    'org.apache.river:mahalo:2.2.2'

    service(name: 'pyCAPS') {
        interfaces {
            classes 'mil.afrl.mstc.open.gcaps.PyCAPS'
            artifact 'mil.afrl.mstc.open:gcaps:0.1'
        }
        implementation(class: 'mil.afrl.mstc.open.gcaps.service.PyCAPSService') {
            artifact 'mil.afrl.mstc.open:gcaps:0.1'
        }

        parameters {
            parameter name: "native.dist",
                      value: System.getProperty("rio.home")+'/../../native-lib-dist-open-6.2'
        }

        association(type: "requires",
                    serviceType: "net.jini.space.JavaSpace05",
                    property: "javaSpace", name: "PySpace")

        association(type: "requires",
                    serviceType: "net.jini.core.transaction.server.TransactionManager",
                    property: "transactionManager", name: "Mahalo")

        maintain 1
    }

    service(name: 'PySpace') {
        interfaces {
            classes 'net.jini.space.JavaSpace05'
            artifact ref: 'outrigger-dl'
        }

        implementation(class: 'com.sun.jini.outrigger.TransientOutriggerImpl') {
            artifact ref: 'outrigger-impl'
        }

        maintain 1

    }

    service(name:'Mahalo') {
        interfaces {
            classes 'net.jini.core.transaction.server.TransactionManager'
            artifact ref: 'mahalo-dl'
        }
        implementation(class: 'com.sun.jini.mahalo.TransientMahaloImpl') {
            artifact ref: 'mahalo-impl'
        }
        maintain 1
        maxPerMachine 1
    }
}