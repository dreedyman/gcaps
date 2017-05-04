#!/usr/bin/env groovy
@Grab('mil.afrl.mstc.open:gcaps:0.1')
@Grab('net.java.dev.jna:jna:4.4.0')
@Grab('com.google.code.gson:gson:2.8.0')

import mil.afrl.mstc.open.gcaps.*
import mil.afrl.mstc.open.gcaps.jna.CapsLibrary

println "jna.library.path: ${System.getProperty("jna.library.path")}"

GCaps analysis = new GCaps()

OS.waitOnInput(60)

String projectName = "FrictionAnalysisTest"
File scriptDir = new File(getClass().protectionDomain.codeSource.location.path).parentFile
String projectRoot = scriptDir.parentFile.parentFile.parentFile.path
String projectDir = "${projectRoot}/build/$projectName"

analysis.loadCAPS("${projectRoot}/csmData/frictionWingTailFuselage.csm", projectName)
def aim = ["aim"          : "frictionAIM",
           "analysisDir"  : projectDir,
           "capsIntent"   : CapsLibrary.CapsFidelity.LINEARAERO]

analysis.loadAIM(aim)
println ("Setting Mach & Altitude Values")
//analysis.setGeometryVal("area", 10.0)
analysis.setAnalysisVal("Mach", [0.5, 1.5])
analysis.setAnalysisVal("Altitude", [30.0, 60.0])

println "Run pre-analysis"
analysis.preAnalysis()

if (System.getProperty("verbose") != null)
    analysis.print()

/* ####### Run Friction #################### */
File workingDir = new File(projectDir)

/* Create symbolic links to files needed to run astros */
File nativeLibs = new File("${System.getProperty("native.lib.dist")}")
File target = new File(workingDir, "friction")
if (!target.exists()) {
    File source = new File(nativeLibs, "sorcer-friction.exe")
    OS.symlink(source, target)
}
String frictionCommand = "friction frictionInput.txt frictionOutput.txt > Info.out"
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