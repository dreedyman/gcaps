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
package mil.afrl.mstc.open.gcaps.jna

import com.google.gson.Gson
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference

/**
 *
 * @author Dennis Reedy
 */
class GCaps {
    PointerByReference capsReference
    PointerByReference analysisRef
    static CapsLibrary cCAPS
    static {
        cCAPS = new CapsLibrary()
    }

    def loadCAPS(String capsFile, String projectName, CAPSOut out) {
        loadCAPS(capsFile, projectName)
        if(out)
            setVerbosity(out)
    }

    def loadCAPS(String capsFile, String projectName) {
        capsReference = new PointerByReference()
        int result = cCAPS.caps_open(capsFile, projectName==null?capsFile:projectName, capsReference)
        if(result!= CAPSErrors.CAPS_SUCCESS.code)
            println "caps_open: ${CAPSErrors.valueOf(result)}"
        result
    }

    def loadAIM(aim) {
        String name = aim["aim"] as String
        int capsIntent = 0
        if(aim["capsIntent"]!=null)
            capsIntent = aim["capsIntent"] as int
        String analysisDir = aim["analysisDir"] as String
        File dir = new File(analysisDir)
        if(!dir.exists()) {
            if(dir.mkdirs())
                println "Created ${dir.path}"
        }
        CapsObject.ByReference byReference = new CapsObject.ByReference(capsReference.value)
        analysisRef = new PointerByReference()
        int result = cCAPS.caps_load(byReference, asPointer(name), asPointer(analysisDir), new Pointer(), capsIntent, 0, (PointerByReference)null, analysisRef)
        if(result!= CAPSErrors.CAPS_SUCCESS.code)
            println "caps_load: ${CAPSErrors.valueOf(result)}"
        return result
    }

    def setGeometryVal(String varName, varValue) {
        doSetAnalysisVal(varName, CapsLibrary.CapsSType.GEOMETRYIN, varValue)
    }

    def setAnalysisVal(String varName, ...varValues) {
        varValues.each { varValue ->
            doSetAnalysisVal(varName, CapsLibrary.CapsSType.ANALYSISIN, varValue)
        }
    }

    private doSetAnalysisVal(String varName, int capsSType, varValue) {
        CapsObject.ByReference byReference = new CapsObject.ByReference(analysisRef.value)
        PointerByReference mObjRef = new PointerByReference()
        int result = cCAPS.caps_childByName(byReference,
                                            CapsLibrary.CapsOType.VALUE,
                                            capsSType,
                                            varName,
                                            mObjRef)
        if(result!= CAPSErrors.CAPS_SUCCESS.code) {
            println "caps_childByName: ${CAPSErrors.valueOf(result)}"
            throw new IllegalStateException("caps_childByName for $varName, [$varValue] failed: ${CAPSErrors.valueOf(result)}")
        }
        CapsObject.ByReference mObj = new CapsObject.ByReference(mObjRef.value)
        IntByReference vType = new IntByReference()
        IntByReference vLen = new IntByReference()
        PointerByReference data = new PointerByReference()
        PointerByReference units = new PointerByReference()
        IntByReference nErr = new IntByReference()
        PointerByReference errors = new PointerByReference()
        result = cCAPS.caps_getValue(mObj, vType, vLen, data, units, nErr, errors)
        if(result!= CAPSErrors.CAPS_SUCCESS.code) {
            println "caps_getValue: ${CAPSErrors.valueOf(result)}, nErr: ${nErr.value}"
            throw new IllegalStateException("caps_getValue for $varName, [$varValue] failed: ${CAPSErrors.valueOf(result)}")
        }
        def value = []
        if(!(varValue instanceof List))
            value << varValue
        else
            value = varValue
        int numRows = value.size()
        int numCols = 1
        if(value.get(0) instanceof List)
            numCols = ((List)value.get(0)).size()

        CAPSVtype capsVtype = CAPSVtype.valueOf(vType.value)
        println "\t${varName} type: ${varValue.getClass().getName()}, vType: ${capsVtype.name()}, numRows : ${numRows}, numCol: ${numCols}"

        Pointer dataPointer
        switch(capsVtype) {
            case CAPSVtype.Boolean:
                dataPointer = new Memory(numRows * Native.getNativeSize(Integer.TYPE))
                for(int i=0; i<numRows; i++) {
                    dataPointer.setInt(i, value.get(i)?1:0)
                }
                break
            case CAPSVtype.Integer:
                dataPointer = new Memory(numRows * Native.getNativeSize(Integer.TYPE))
                for(int i=0; i<numRows; i++) {
                    dataPointer.setInt(i, value.get(i) as Integer)
                }
                break
            case CAPSVtype.Double:
                dataPointer = new Memory(numRows * Native.getNativeSize(Double.TYPE))
                for(int i=0; i<numRows; i++) {
                    dataPointer.setDouble(i, value.get(i) as Double)
                }
                break
            case CAPSVtype.String:
                dataPointer = asPointer(value.get(0) as String)
                break
            case CAPSVtype.Tuple:
                CapsTuple.ByReference tupleRef = new CapsTuple.ByReference()
                CapsTuple[] tuples = tupleRef.toArray(numRows) as CapsTuple[]
                for(int i=0; i<numRows; i++) {
                    Map map = (Map)value.get(i)
                    String name = map.keySet().toArray()[0]
                    tuples[i].name = asPointer(name)
                    def t = map.get(name)
                    if (t instanceof String ||
                        t instanceof Integer ||
                        t instanceof Float) {
                        tuples[i].value = asPointer(t.toString())
                    } else {
                        String jsonIzed = toJSON(t)
                        tuples[i].value = asPointer(jsonIzed)
                    }
                }
                println "\tcaps_setValue"
                result = cCAPS.caps_setValue(mObj, numRows, numCols, tupleRef)
                if (result != CAPSErrors.CAPS_SUCCESS.code) {
                    throw new IllegalStateException("caps_setValue for $varName, [$varValue] failed: ${CAPSErrors.valueOf(result)}")
                }
                return result

        }
        if(dataPointer!=null) {
            result = cCAPS.caps_setValue(mObj, numRows, numCols, dataPointer)
            if (result != CAPSErrors.CAPS_SUCCESS.code) {
                throw new IllegalStateException("caps_setValue for $varName, [$varValue] failed: ${CAPSErrors.valueOf(result)}")
            }
        } else {
            result = CAPSErrors.CAPS_BADTYPE.code
        }
        result
    }

    def preAnalysis() {
        CapsObject.ByReference byReference = new CapsObject.ByReference(analysisRef.value)
        IntByReference nErr = new IntByReference()
        PointerByReference errors = new PointerByReference()
        int result = cCAPS.caps_preAnalysis(byReference, nErr, errors)
        if(result!= CAPSErrors.CAPS_SUCCESS.code) {
            println "caps_preAnalysis: ${CAPSErrors.valueOf(result)}, nErr: ${nErr.value}"
        }
        result
    }

    def postAnalysis() {
        CapsObject.ByReference aRef = new CapsObject.ByReference(analysisRef.value)
        PointerByReference name = new PointerByReference()
        IntByReference type = new IntByReference()
        IntByReference subType = new IntByReference()
        CapsObject.ByReference link = new CapsObject.ByReference()
        CapsObject.ByReference parent = new CapsObject.ByReference()
        CapsOwn.ByReference last = new CapsOwn.ByReference()
        int result = cCAPS.caps_info(aRef, name, type, subType, link, parent, last)

        if(result!= CAPSErrors.CAPS_SUCCESS.code) {
            println "caps_info: ${CAPSErrors.valueOf(result)}"
            return result
        }
        CapsOwn.ByValue owner = new CapsOwn.ByValue(last.pointer)
        IntByReference nErr = new IntByReference()
        CapsErrs.ByReference errors = new CapsErrs.ByReference()
        result = cCAPS.caps_postAnalysis(aRef, owner, nErr, errors)
        if(result!= CAPSErrors.CAPS_SUCCESS.code) {
            println "caps_postAnalysis: ${CAPSErrors.valueOf(result)}, nErr: ${nErr.value}"
        }
        result
    }

    def getAnalysisOutVal(String varName) {
        if(varName!=null) {
            CapsObject.ByReference byReference = new CapsObject.ByReference(analysisRef.value)
            PointerByReference mObjRef = new PointerByReference()
            int result = cCAPS.caps_childByName(byReference,
                                                CapsLibrary.CapsOType.VALUE,
                                                CapsLibrary.CapsSType.ANALYSISOUT,
                                                varName,
                                                mObjRef)
            if (result != CAPSErrors.CAPS_SUCCESS.code) {
                println "caps_childByName: ${CAPSErrors.valueOf(result)}"
                return null
            }
            CapsObject.ByReference mObj = new CapsObject.ByReference(mObjRef.value)
            IntByReference vType = new IntByReference()
            IntByReference vLen = new IntByReference()
            PointerByReference data = new PointerByReference()
            PointerByReference units = new PointerByReference()
            IntByReference nErr = new IntByReference()
            PointerByReference errors = new PointerByReference()
            result = cCAPS.caps_getValue(mObj, vType, vLen, data, units, nErr, errors)
            if (result != CAPSErrors.CAPS_SUCCESS.code) {
                println "caps_getValue: ${CAPSErrors.valueOf(result)}, nErr: ${nErr.value}"
                return null
            }
            IntByReference dim = new IntByReference()
            IntByReference lfix = new IntByReference()
            IntByReference sfix = new IntByReference()
            IntByReference type = new IntByReference()
            IntByReference numRows = new IntByReference()
            IntByReference numCols = new IntByReference()

            result = cCAPS.caps_getValueShape(mObj, dim, lfix, sfix, type, numRows, numCols)
            if (result != CAPSErrors.CAPS_SUCCESS.code) {
                println "caps_getValueShape: ${CAPSErrors.valueOf(result)}, nErr: ${nErr.value}"
                return null
            }
            CAPSVtype capsVType = CAPSVtype.valueOf(vType.value)
            switch (capsVType) {
                case CAPSVtype.Boolean:
                    break
                case CAPSVtype.Integer:
                    return data.value.getIntArray(0, numRows.value)
                case CAPSVtype.Double:
                    return data.value.getDoubleArray(0, numRows.value)
                case CAPSVtype.String:
                    return data.value.getStringArray(0, numRows.value)
                case CAPSVtype.Tuple:
                    break
            }
        } else {
            println "Build a dictionary not supported yet"
        }
        return null
    }

    def print() {
        CapsObject.ByReference byReference = new CapsObject.ByReference(capsReference.value)
        cCAPS.printObjects(byReference, 4)
    }

    def close() {
        CapsObject.ByReference capsObject = new CapsObject.ByReference(capsReference.getValue())
        int result = cCAPS.caps_close(capsObject)
        if(result!= CAPSErrors.CAPS_SUCCESS.code) {
            println "caps_close: ${CAPSErrors.valueOf(result)}"
        }
        result
    }

    def setVerbosity(CAPSOut capsOut) {
        CapsObject.ByReference capsObject = new CapsObject.ByReference(capsReference.getValue())
        cCAPS.caps_outLevel(capsObject, capsOut.level)
    }

    private Pointer asPointer(String s) {
        Pointer p = new Memory(s.length() + 1) // WARNING: assumes ascii-only string
        p.setString(0, s)
        p
    }

    private String toJSON(value) {
        Gson gson = new Gson()
        return gson.toJson(value)
    }

}
