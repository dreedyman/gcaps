import os
import mstc_eng
from mstc_eng import analysis

projectName = "FrictionAnalysisTest"
myAnalysis = analysis(projectName)

myAnalysis.setCSM(os.getcwd()+"/csmData/frictionWingTailFuselage.csm")
myAnalysis.setAIM({"aim"           : "frictionAIM",
                   "analysisDir"   : projectName,
                   "capsIntent"    : "LINEARAERO"})

myAnalysis.setGeometryVal("area", 10.0)

myAnalysis.setAnalysisVal("Mach", [0.5, 1.5])

myAnalysis.setAnalysisVal("Altitude", [9000, 18200.0], units= "m")

myAnalysis.addOutputs("CDtotal", "CDform", "CDfric")

mstc_eng.submit(myAnalysis)

Cdtotal = myAnalysis.getAnalysisOutVal("CDtotal")
CdForm  = myAnalysis.getAnalysisOutVal("CDform")
CdFric  = myAnalysis.getAnalysisOutVal("CDfric")

print("Total drag =", Cdtotal )
print("Form drag =", CdForm)
print("Friction drag =", CdFric)
