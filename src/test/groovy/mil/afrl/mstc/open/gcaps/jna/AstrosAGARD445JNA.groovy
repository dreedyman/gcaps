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

import mil.afrl.mstc.open.gcaps.OS

/**
 *
 * @author Dennis Reedy
 */
class AstrosAGARD445JNA extends MSTCAnalysisJNA {

    def init(options) {
        super.init(options)
        int result = analysis.loadCAPS("${projectDataRoot}/feaAGARD445.csm", projectName)
        if(result!=CAPSErrors.CAPS_SUCCESS.code)
            throw new IllegalStateException("loadCAPS failed ${CAPSError.valueOf(result)}" as String)
        def aim = ["aim": "astrosAIM",
                   "altName" : "astros",
                   "analysisDir" : "${projectDir}/$projectName",
                   "capsFidelity" : CapsLibrary.CapsFidelity.ALL
        ]
        analysis.loadAIM(aim)
        /* Set project name so a mesh file is generated */
        analysis.setAnalysisVal("Proj_Name", projectName)

        /*  Set meshing parameters */
        analysis.setAnalysisVal("Edge_Point_Max", 10)
        analysis.setAnalysisVal("Edge_Point_Min", 6)
        analysis.setAnalysisVal("Quad_Mesh", true)
        analysis.setAnalysisVal("Tess_Params", [0.25, 0.01, 15])

        /*  Set analysis type */
        analysis.setAnalysisVal("Analysis_Type", "Modal")

        /* Set analysis inputs */
        def eigen = [ "extractionMethod"     : "MGIV", //"Lanczos",
                      "frequencyRange"       : [0.1, 200],
                      "numEstEigenvalue"     : 1,
                      "numDesiredEigenvalue" : 2,
                      "eigenNormaliztion"    : "MASS",
                      "lanczosMode"          : 2,      // Default - not necesssary
                      "lanczosType"          : "DPB"]  //Default - not necesssary

        analysis.setAnalysisVal("Analysis", ["EigenAnalysis": eigen])

        /* Set materials */
        def mahogany    = ["materialType"        : "orthotropic",
                           "youngModulus"        : 0.457E6,
                           "youngModulusLateral" : 0.0636E6,
                           "poissonRatio"        : 0.31,
                           "shearModulus"        : 0.0637E6,
                           "shearModulusTrans1Z" : 0.00227E6,
                           "shearModulusTrans2Z" : 0.00227E6,
                           "density"             : 3.5742E-5]

        analysis.setAnalysisVal("Material", ["Mahogany": mahogany])

        /* Set properties */
        def shell  = ["propertyType"        : "Shell",
                      "membraneThickness"   : 0.82,
                      "material"            : "mahogany",
                      "bendingInertiaRatio" : 1.0,     // Default - not necessary
                      "shearMembraneRatio"  : 5.0/6.0] // Default - not necessary

        analysis.setAnalysisVal("Property", ["yatesPlate": shell])

        /* Set constraints */
        def constraint = ["groupName"     : "constEdge",
                          "dofConstraint" : 123456]

        analysis.setAnalysisVal("Constraint", ["edgeConstraint": constraint])
    }

    def result() {
        println "Run pre-analysis"
        analysis.preAnalysis()

        if(System.getProperty("verbose")!=null)
            analysis.print()

        int count = 0
        while(count<5) {
            print "."
            Thread.sleep(1000)
            count++
        }

        /* ####### Run Astros #################### */
        File cwd = new File(projectDir)

        /* Create symbolic links to files needed to run astros */
        String nativeLib = System.getProperty("native.open.dist")+"/"+OS.get()
        String astrosInstallDir = "${nativeLib}/astros/12.5/system/"
        def files = ["astros.exe", // Executable
                     "ASTRO.D01",  // *.DO1 file
                     "ASTRO.IDX"]  // *.IDX file
        files.each { f ->
            File target = new File(cwd, projectName+"/"+f)
            if(!target.exists()) {
                File source = new File(astrosInstallDir, f)
                OS.symlink(source, target)
            }
        }
        String astrosCommand = "astros.exe < " + projectName +  ".dat > " + projectName + ".out"
        File workingDir = new File(cwd, projectName)
        println ("Running Astros... in ${workingDir.path}")
        int result = OS.exec(astrosCommand, workingDir)
        println ("Done running Astros!, result: $result")
        /* ####################################### */

        /* Run post-analysis */
        println ("Run post-analysis")
        analysis.postAnalysis()

        /* Get Eigen-frequencies */
        println ("Getting results natural frequencies...")
        return analysis.getAnalysisOutVal("EigenFrequency")
    }

    def close() {
        analysis.close()
    }
}
