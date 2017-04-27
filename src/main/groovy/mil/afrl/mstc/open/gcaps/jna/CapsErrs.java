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
package mil.afrl.mstc.open.gcaps.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
/**
 * @author Dennis Reedy
 */
public class CapsErrs extends Structure {
    /** number of errors in this structure */
    public int nError;
    /**
     * the errors<br>
     * C type : capsError*
     */
    public CapsError.ByReference errors;
    public CapsErrs() {
        super();
    }
    protected List<String> getFieldOrder() {
        return Arrays.asList("nError", "errors");
    }

    public CapsErrs(Pointer peer) {
        super(peer);
    }
    public static class ByReference extends CapsErrs implements Structure.ByReference {
        public ByReference() { }
        public ByReference(Pointer p) { super(p); read(); }
    }
    public static class ByValue extends CapsErrs implements Structure.ByValue {
        public ByValue() { }
        public ByValue(Pointer p) { super(p); read(); }
    }
}
