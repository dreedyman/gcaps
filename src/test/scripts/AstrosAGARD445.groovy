#!/usr/bin/env groovy
@Grab('mil.afrl.mstc.open:gcaps:0.1')
@Grab('net.java.dev.jna:jna:4.2.2')
@Grab('com.google.code.gson:gson:2.8.0')

import mil.afrl.mstc.open.gcaps.*
import mil.afrl.mstc.open.gcaps.jna.*

println "jna.library.path: ${System.getProperty("jna.library.path")}"

GCaps gCaps = new GCaps()

OS.waitOnInput(60)

File scriptDir = new File(getClass().protectionDomain.codeSource.location.path).parentFile
String projectRoot = scriptDir.parentFile.parentFile.parentFile.path

String projectName = "AstrosModalAGARD445"
String projectDir = "${projectRoot}/build/$projectName"
gCaps.loadCAPS("${projectRoot}/csmData/feaAGARD445.csm", projectName, CAPSOut.DEBUG)

def aim = ["aim"        : "astrosAIM",
           "altName"    : "astros",
           "analysisDir": projectDir,
           "capsIntent" : CapsLibrary.CapsFidelity.ALL
]
println("Load AIM...")
gCaps.loadAIM(aim)

println("Set analysis variables...")
/* Set project name so a mesh file is generated */
gCaps.setAnalysisVal("Proj_Name", projectName)

/*  Set meshing parameters */
gCaps.setAnalysisVal("Edge_Point_Max", 10)
gCaps.setAnalysisVal("Edge_Point_Min", 6)
gCaps.setAnalysisVal("Quad_Mesh", true)
gCaps.setAnalysisVal("Tess_Params", [0.25, 0.01, 15])

/*  Set analysis type */
gCaps.setAnalysisVal("Analysis_Type", "Modal")

/* Set analysis inputs */
def eigen = ["extractionMethod"    : "MGIV", //"Lanczos",
             "frequencyRange"      : [0.1, 200],
             "numEstEigenvalue"    : 1,
             "numDesiredEigenvalue": 2,
             "eigenNormaliztion"   : "MASS",
             "lanczosMode"         : 2,      // Default - not necesssary
             "lanczosType"         : "DPB"]  //Default - not necesssary

gCaps.setAnalysisVal("Analysis", ["EigenAnalysis": eigen])

/* Set materials */
def mahogany = ["materialType"       : "orthotropic",
                "youngModulus"       : 0.457E6,
                "youngModulusLateral": 0.0636E6,
                "poissonRatio"       : 0.31,
                "shearModulus"       : 0.0637E6,
                "shearModulusTrans1Z": 0.00227E6,
                "shearModulusTrans2Z": 0.00227E6,
                "density"            : 3.5742E-5]

gCaps.setAnalysisVal("Material", ["Mahogany": mahogany])

/* Set properties */
def shell = ["propertyType"       : "Shell",
             "membraneThickness"  : 0.82,
             "material"           : "mahogany",
             "bendingInertiaRatio": 1.0,     // Default - not necesssary
             "shearMembraneRatio" : 5.0 / 6.0] // Default - not necesssary

gCaps.setAnalysisVal("Property", ["yatesPlate": shell])

/* Set constraints */
def constraint = ["groupName"    : "constEdge",
                  "dofConstraint": 123456]

gCaps.setAnalysisVal("Constraint", ["edgeConstraint": constraint])

/* Run pre-analysis */
println("Run pre-analysis...")
gCaps.preAnalysis()

if (System.getProperty("verbose") != null)
    gCaps.print()

/* ####### Run Astros #################### */
File cwd = new File(projectDir)

/* Create symbolic links to files needed to run astros */
File nativeLibDist = new File(System.getProperty("native.lib.dist"))
String astrosInstallDir = "${nativeLibDist.canonicalPath}/astros/12.5/system/"
def files = ["astros.exe", // Executable
             "ASTRO.D01",  // *.DO1 file
             "ASTRO.IDX"]  // *.IDX file
files.each { f ->
    File target = new File(cwd, f)
    if (!target.exists()) {
        File source = new File(astrosInstallDir, f)
        OS.symlink(source, target)
    }
}
println("Running Astros in ${cwd.path} ...")
String astrosCommand = "astros.exe < " + projectName + ".dat > " + projectName + ".out"
int result = OS.exec(astrosCommand, cwd)
println("Done running Astros!, result: $result")
/* ####################################### */

/* Run post-analysis */
println("Run post-analysis")
gCaps.postAnalysis()

/* Get Eigen-frequencies */
println("Getting results natural frequencies...")
def naturalFreq = gCaps.getAnalysisOutVal("EigenFrequency")
if (naturalFreq != null) {
    int mode = 1
    naturalFreq.each { n ->
        println(String.format("Natural freq (Mode %d) = %s (Hz)", mode, n))
        mode += 1
    }
} else {
    println "No data returned"
}
gCaps.close()
