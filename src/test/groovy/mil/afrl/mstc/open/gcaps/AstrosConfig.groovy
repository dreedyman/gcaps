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
class AstrosConfig {
    String projectDataRoot
    String projectDir
    String projectName
    String url

    AstrosConfig(String projectDataRoot, String projectDir, String projectName, String url) {
        this.projectDataRoot = projectDataRoot
        this.projectDir = projectDir
        this.projectName = projectName
        this.url = url
    }

    def get() {
        Builder caps = new Builder()
        caps.setProjectName(projectName)
                .setCSMData(projectDataRoot+'/feaAGARD445.csm')
                .setAIM("astrosAIM", "astros", "ALL")
        

        caps.setAnalysisVal("Proj_Name", projectName)
                .setAnalysisVal("Edge_Point_Max", 10)
                .setAnalysisVal("Edge_Point_Min", 6)
                .setAnalysisVal("Quad_Mesh", true)
                .setAnalysisVal("Tess_Params", [0.25, 0.01, 15])

        /*  Set analysis type */
        caps.setAnalysisVal("Analysis_Type", "Modal")

        /* Set analysis inputs */
        def eigen = [ "extractionMethod"     : "MGIV", //"Lanczos",
                      "frequencyRange"       : [0.1, 200],
                      "numEstEigenvalue"     : 1,
                      "numDesiredEigenvalue" : 2,
                      "eigenNormaliztion"    : "MASS",
                      "lanczosMode"          : 2,      // Default - not necessary
                      "lanczosType"          : "DPB"]  //Default - not necessary

        caps.setAnalysisVal("Analysis", ["EigenAnalysis": eigen])

        /* Set materials */
        def mahogany    = ["materialType"        : "orthotropic",
                           "youngModulus"        : 0.457E6,
                           "youngModulusLateral" : 0.0636E6,
                           "poissonRatio"        : 0.31,
                           "shearModulus"        : 0.0637E6,
                           "shearModulusTrans1Z" : 0.00227E6,
                           "shearModulusTrans2Z" : 0.00227E6,
                           "density"             : 3.5742E-5]

        caps.setAnalysisVal("Material", ["Mahogany": mahogany])

        /* Set properties */
        def shell  = ["propertyType"        : "Shell",
                      "membraneThickness"   : 0.82,
                      "material"            : "mahogany",
                      "bendingInertiaRatio" : 1.0,     // Default - not necessary
                      "shearMembraneRatio"  : 5.0/6.0] // Default - not necessary

        caps.setAnalysisVal("Property", ["yatesPlate": shell])

        /* Set constraints */
        def constraint = ["groupName"     : "constEdge",
                          "dofConstraint" : 123456]

        caps.setAnalysisVal("Constraint", ["edgeConstraint": constraint])

        caps.addAnalysisOutVal("EigenFrequency")

        return caps.build()
    }


}
