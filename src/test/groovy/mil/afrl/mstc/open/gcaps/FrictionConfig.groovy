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

import mil.afrl.mstc.open.gcaps.client.Builder


/**
 *
 * @author Dennis Reedy
 */
class FrictionConfig {
    String projectDataRoot
    String projectDir
    String projectName

    FrictionConfig(options) {
        projectName = options["projectName"]
        projectDataRoot = options['projectDataRoot']
        projectDir = options['projectDir']
    }

    def get() {
        Builder caps = new Builder()
        caps.setProjectName(projectName)
                .setNativeApp("friction")
                .setCSMData(projectDataRoot+'/frictionWingTailFuselage.csm')
                .setAIM("frictionAIM", "LINEARAERO")


        caps.setGeometryVal("area", 10.0)
                .setAnalysisVal("Mach", [0.5, 1.5])
                .setAnalysisVal("Altitude", [9000, 18200.0], "m")

        caps.addAnalysisOutVal("CDtotal", "CDform", "CDfric")

        return caps.build()
    }

}