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

import mil.afrl.mstc.open.gcaps.jna.CapsLibrary

/**
 *
 * @author Dennis Reedy
 */
class Friction extends MSTCAnalysis {

    def init(options) {
        super.init(options)
        analysis.loadCAPS("${projectDataRoot}/frictionWingTailFuselage.csm", projectName)
        def aim = ["aim"          : "frictionAIM",
                   "analysisDir"  : "FrictionAnalysisTest",
                   "capsFidelity" : CapsLibrary.CapsFidelity.LINEARAERO]

        analysis.loadAIM(aim)
        println ("Setting Mach & Altitude Values")
        analysis.setGeometryVal("area", 10.0)
        analysis.setAnalysisVal("Mach", [0.5, 1.5])
        analysis.setAnalysisVal("Altitude", [30.0, 60.0])
    }

    def result() {
        println "Run pre-analysis"
        analysis.preAnalysis()

        if (System.getProperty("verbose") != null)
            analysis.print()

        /* ####### Run Friction #################### */
        File cwd = new File(projectDir)

        /* Create symbolic links to files needed to run astros */
        File nativeLibs = new File("${System.getProperty("native.lib.dist")}")
        File target = new File(cwd, projectName + "/friction")
        if (!target.exists()) {
            File source = new File(nativeLibs, "sorcer-friction.exe")
            OS.symlink(source, target)
        }
        String frictionCommand = "friction frictionInput.txt frictionOutput.txt > Info.out"
        File workingDir = new File(cwd, projectName)
        println("Running Friction... in ${workingDir.path}")
        int result = OS.exec(frictionCommand, workingDir)
        println("Done running Friction!, result: $result")
        /* ####################################### */

        /* Run post-analysis */
        println("Run post-analysis")
        analysis.postAnalysis()

        /* Get Output Data from Friction */
        println("Getting output...")
        def outputs = [:]
        outputs.put("cdTotal", analysis.getAnalysisOutVal("CDtotal"))
        outputs.put("cdForm", analysis.getAnalysisOutVal("CDform"))
        outputs.put("cdFric",analysis.getAnalysisOutVal("CDfric"))

        println("Total drag = ${outputs['cdTotal']}")
        println("Form drag = ${outputs['cdForm']}")
        println("Friction drag = ${outputs['cdFric']}")
        outputs
    }

    def close() {
        analysis.close()
    }

}