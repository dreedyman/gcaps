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
package mil.afrl.mstc.open.gcaps.entry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dennis Reedy
 */
public class Message implements Comparable<Message>,Serializable {
    static final long serialVersionUID = 1L;
    private String request;
    private Map response = new HashMap();
    private Integer index;

    public Message(Integer index) {
        this.index = index;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public Map getResponse() {
        return response;
    }

    @SuppressWarnings("unchecked")
    public void setResponse(Map response) {
        this.response.putAll(response);
    }

    public Integer getIndex() {
        return index;
    }

    @Override public int compareTo(Message o) {
        return index<o.index?-1:1;
    }
}
