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
import com.sun.jna.ptr.PointerByReference;
import java.util.Arrays;
import java.util.List;
/**
 * @author Dennis Reedy
 */
public class CapsError extends Structure {
    /**
     * the offending object pointer<br>
     * C type : capsObject*
     */
    public CapsObject.ByReference errObj;
    /** index to offending struct -- AIM */
    public int index;
    /** the number of error strings */
    public int nLines;
    /**
     * the error strings<br>
     * C type : char**
     */
    public PointerByReference lines;
    public CapsError() {
        super();
    }
    protected List<String> getFieldOrder() {
        return Arrays.asList("errObj", "index", "nLines", "lines");
    }

    public CapsError(Pointer peer) {
        super(peer);
    }
    public static class ByReference extends CapsError implements Structure.ByReference {
        public ByReference() { }
        public ByReference(Pointer p) { super(p); read(); }
    }
    public static class ByValue extends CapsError implements Structure.ByValue {
        public ByValue() { }
        public ByValue(Pointer p) { super(p); read(); }
    }
}
