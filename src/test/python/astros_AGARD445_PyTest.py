from __future__ import print_function
import os
import mstc_eng
from mstc_eng import analysis

#analysisVals = []
#def setAnalysisVal(name, value, units=None):
#    entry = {'name': name, 'value': value}
#    analysisVals.append(entry)

projectName = "AstrosModalAGARD445"
myAnalysis = analysis(projectName)
myAnalysis.setCSM(os.getcwd()+"/csmData/feaAGARD445.csm")
myAnalysis.setAIM({"aim"       : "astrosAIM",
                   "altName"   : "astros",
                   "capsIntent": "ALL"})

# Set project name so a mesh file is generated 
myAnalysis.setAnalysisVal("Proj_Name", projectName)

# Set meshing parameters
myAnalysis.setAnalysisVal("Edge_Point_Max", 10)
myAnalysis.setAnalysisVal("Edge_Point_Min", 6)

myAnalysis.setAnalysisVal("Quad_Mesh", True)

myAnalysis.setAnalysisVal("Tess_Params", [.25,.01,15])

# Set analysis type
myAnalysis.setAnalysisVal("Analysis_Type", "Modal")

# Set analysis inputs
eigen = { "extractionMethod"     : "MGIV", # "Lanczos",   
          "frequencyRange"       : [0.1, 200], 
          "numEstEigenvalue"     : 1,
          "numDesiredEigenvalue" : 2, 
          "eigenNormaliztion"    : "MASS", 
	      "lanczosMode"          : 2,  # Default - not necesssary
          "lanczosType"          : "DPB"} # Default - not necesssary

myAnalysis.setAnalysisVal("Analysis", {"EigenAnalysis": eigen})

# Set materials 
mahogany    = {"materialType"        : "orthotropic", 
               "youngModulus"        : 0.457E6 ,
               "youngModulusLateral" : 0.0636E6,
               "poissonRatio"        : 0.31,                
               "shearModulus"        : 0.0637E6,
               "shearModulusTrans1Z" : 0.00227E6,
               "shearModulusTrans2Z" : 0.00227E6,
               "density"             : 3.5742E-5}

myAnalysis.setAnalysisVal("Material", {"Mahogany": mahogany})

# Set properties
shell  = {"propertyType" : "Shell", 
          "membraneThickness" : 0.82, 
          "material"        : "mahogany", 
          "bendingInertiaRatio" : 1.0, # Default - not necesssary           
          "shearMembraneRatio"  : 5.0/6.0} # Default - not necesssary 

myAnalysis.setAnalysisVal("Property", {"yatesPlate": shell})

# Set constraints
constraint = {"groupName" : "constEdge",
              "dofConstraint" : 123456}

myAnalysis.setAnalysisVal("Constraint", {"edgeConstraint": constraint})

myAnalysis.addOutput("EigenFrequency")

mstc_eng.submit(myAnalysis)

naturalFreq = myAnalysis.getAnalysisOutVal("EigenFrequency")
mode = 1
for i in naturalFreq:
    print ("Natural freq (Mode {:d}) = ".format(mode) + '{:.2f} '.format(i) + "(Hz)")
    mode += 1