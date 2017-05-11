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
package mil.afrl.mstc.open.gcaps.proxy

import com.sun.jini.admin.DestroyAdmin
import mil.afrl.mstc.open.gcaps.client.Client
import mil.afrl.mstc.open.gcaps.PyCAPS
import mil.afrl.mstc.open.gcaps.PyCAPSException
import mil.afrl.mstc.open.gcaps.PyCAPSFactory
import net.jini.admin.Administrable
import net.jini.id.ReferentUuid
import net.jini.id.Uuid
import net.jini.id.UuidFactory

/**
 *
 * @author Dennis Reedy
 */
@SuppressWarnings("unused")
class PyCAPSProxy implements PyCAPSFactory, PyCAPS, ReferentUuid, Serializable {
    static final long serialVersionUID = 1L
    private final String address
    private final int port
    private final Uuid uuid
    private Client client
    private final PyCAPSFactory service

    static PyCAPSProxy create(String address, int port) {
        return create(address, port, null)
    }

    static PyCAPSProxy create(String address, int port, PyCAPSFactory service) {
        return new PyCAPSProxy(address, port, service)
    }

    private PyCAPSProxy(String address, int port, PyCAPSFactory service) {
        this.address = address
        this.port = port
        this.uuid =  UuidFactory.generate()
        this.service = service
        client = new Client(address, port)
    }

    @Override
    submit(String data) throws PyCAPSException {
        return client.submit(data)
    }

    @Override
    shutdown() {
        if(service==null) {
            client.shutdown()
        } else {
            ((DestroyAdmin)((Administrable)service).admin).destroy()
        }
    }

    boolean equals(Object o) {
        if (!(o instanceof ReferentUuid))
            return false
        ReferentUuid that = (ReferentUuid)o
        return uuid==that.getReferentUuid()
    }

    int hashCode() {
        return uuid != null ? uuid.hashCode() : 0
    }

    Uuid getReferentUuid() {
        return uuid
    }

    @Override
    PyCAPS create() {
        return this
    }
}
