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
package mil.afrl.mstc.open.gcaps.client

import com.google.gson.Gson
import mil.afrl.mstc.open.gcaps.PyCAPSException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Dennis Reedy
 */
class Client implements Serializable {
    static final long serialVersionUID = 1L
    private final String address
    private final int port
    private static Logger logger = LoggerFactory.getLogger(Client.class)

    Client(String address, int port) {
        if(address==null)
            throw new IllegalArgumentException("address cannot be null")
        if(port==0)
            throw new IllegalArgumentException("port cannot be 0")
        this.address = address
        this.port = port
    }

    def submit(String data) {
        String result = null
        logger.info("Create socket: {}, {}", address, port)
        def socket = new Socket(address, port)
        try {
            socket.withStreams { input, output ->
                output << data
                output.flush()
                result = input.newReader().readLine()
/*                output << 'close'
                output.flush()*/
            }
        } finally {
            socket.close()
        }
        logger.debug(result)
        if(result.startsWith("FAIL"))
            throw new PyCAPSException(result)
        Gson gson = new Gson()
        gson.fromJson(result, Map.class)
    }

    def shutdown() {
        logger.info("Create socket: {}, {}", address, port)
        try {
            def socket = new Socket(address, port)
            try {
                socket.withStreams { input, output ->
                    output << 'shutdown'
                    output.flush()
                }
            } finally {
                socket.close()
            }
        } catch(IOException e) {
            logger.warn("Failed creating socket for shutdown request. {}: {}", e.getClass().getName(), e.getMessage())
        }
    }
}
