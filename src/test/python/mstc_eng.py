import json
from pprint import pprint
import os

class analysis:

    def __init__(self, projectName):
        self.projectName = projectName
        self.tricks = []
        self.analysis = {}
        self.analysisVals = []
        self.outputs = []
        self.aimData = []
        self.geometryVals = {}
        self.outputVals = {}

    def setAIM(self, aim):
        self.analysis['aimData'] = aim

    def setCSM(self, csm):
        self.analysis['csmData'] = csm

    def setAnalysisVal(self, name, value, units=None):
        if units is None:
            entry = {'name': name, 'value': value}
        else:
            entry = {'name': name, 'value': value, 'units': units}
        self.analysisVals.append(entry)

    def setGeometryVal(self, name, value):
        self.geometryVals[name] = value

    def addOutput(self, name):
        self.outputs.append(name)

    def addOutputs(self, *names):
        for name in names:
            self.outputs.append(name)

    def getAnalysisOutVal(self, key):
        return self.outputVals[key]

    def build(self):
        self.analysis['projectName'] = self.projectName
        self.analysis['analysisVals'] = self.analysisVals
        self.analysis['outputs'] = self.outputs
        if len(self.geometryVals) > 0:
            self.analysis['geometryVals'] = self.geometryVals
        return self.analysis


def submit(analysis):
    json_string = json.dumps(analysis.build() )
    jsonFile = open("mstc-pycaps.json", 'w')
    jsonFile.write(json_string)
    jsonFile.close()
    outputFile = open("output.json", 'w')
    outputFile.truncate()
    os.system("java -jar build/libs/gcaps-0.1.jar mstc-pycaps.json output.json")
    output = open("output.json", 'r')
    result = json.load(output)
    for key in result:
        analysis.outputVals[key] = result[key]


def dump():
    json_string = json.dumps(analysis.build())
    loaded = json.loads(json_string)
    pprint(loaded)

    #with open('build/astrosJson.txt') as data_file:
    #    data = json.load(data_file)

    #with open('astrosJSON.txt', 'wt') as out:
    #    pprint(data, stream=out)
