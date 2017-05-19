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
/**
 *
 * @author Dennis Reedy
 */
class Builder {
    private analysis = [:]
    private analysisVals = []
    private geometryVals = [:]
    private outputs = []
    private static String PROJECT_NAME="projectName"
    private static String CSM_DATA="csmData"
    private static String AIM="aimData"
    private static String ANALYSIS_VALS="analysisVals"
    private static String GEOMETRY_VALS="geometryVals"
    private static String OUTPUTS="outputs"

    Builder setProjectName(String projectName) {
        analysis.put(PROJECT_NAME, projectName)
        this
    }

    Builder setCSMData(String csmData) {
        analysis.put(CSM_DATA, csmData)
        this
    }

    Builder setAIM(String aimName, String capsIntent) {
        def aim = ["aim": aimName,
                   "capsIntent"  : capsIntent
        ]
        analysis.put(AIM, aim)
        this
    }

    Builder setAIM(String aimName, String altAimName, String capsIntent) {
        def aim = ["aim": aimName,
                   "altName" : altAimName,
                   "capsIntent"  : capsIntent
        ]
        analysis.put(AIM, aim)
        this
    }

    Builder setAnalysisVal(String name, Object value) {
        def analysisVal = ["name": name,
                           "value": value]
        analysisVals << analysisVal
        this
    }

    Builder setAnalysisVal(String name, Object value, String units) {
        def analysisVal = ["name": name,
                           "value": value,
                           "units": units]
        analysisVals << analysisVal
        this
    }

    Builder setGeometryVal(String name, Object value) {
        geometryVals.put(name, value)
        this
    }

    Builder addAnalysisOutVal(String... names) {
        names.each { name ->
            outputs << name
        }
        this
    }

    String build() {
        if(!geometryVals.isEmpty())
            analysis.put(GEOMETRY_VALS, geometryVals)
        if(!analysisVals.isEmpty())
            analysis.put(ANALYSIS_VALS, analysisVals)
        analysis.put(OUTPUTS, outputs)
        Gson gson = new Gson()
        return gson.toJson(analysis)
    }

}
