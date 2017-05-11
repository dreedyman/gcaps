'''
    Socket server that runs pyCAPS from a JSON document
'''
# Import os module
try:
    import os
except:
    print ("Unable to import os module")
    raise SystemError
from pyCAPS import capsProblem
import socket
import sys
from thread import *
from sys import argv
import json
import logging
from pprint import pprint

if len(argv)<3:
    print 'You must provide a filename to write the port to, the location of the native libraries'
    sys.exit()

scriptPath = os.path.dirname(argv[0])
portFileName = argv[1]
nativeLibDist = os.path.abspath(argv[2])

if not nativeLibDist.endswith("/"):
    nativeLibDist = nativeLibDist+"/"

logging.basicConfig(format='%(asctime)s %(message)s', filename=argv[0]+'.log',level=logging.DEBUG)

logging.info("\n=================================\nStarting pyCAPS service....\n=================================")
logging.info("portFileName:  "+portFileName)
logging.info("nativeLibDist: "+nativeLibDist)
keepRunning = True
HOST = ''

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
logging.info('Socket created')

# Bind socket to local host and port
try:
    serverSocket.bind((HOST, 0))
except socket.error as msg:
    logging.error('Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1])
    sys.exit()

port = serverSocket.getsockname()[1]
portFile = open(portFileName, 'w')
portFile.truncate()
portFile.write(str(port))
portFile.close()
logging.info('Socket bind complete, listening on port ' + str(port))

# Start listening on socket
serverSocket.listen(10)
logging.info('Server now listening')

#
# Receives all data from a socket. Might have a defect in that TCP/IP might not send complete buffer.
# TODO: consider changing
#
def recvall(sock):
    BUFF_SIZE = 1024
    data = ""
    while True:
        part = sock.recv(BUFF_SIZE)
        data += part
        if len(part) < BUFF_SIZE:
            break
    return data

#
# Runs native apps from MSTC Eng native open distribution
#
def nativeAppRunner(nativeApp, analysisDir, projectName):
    cwd = os.getcwd()
    os.chdir(analysisDir)
    logging.info("Working directory: "+os.getcwd())

    if nativeApp.lower() == "astros":
        astrosInstallDir = nativeLibDist+"astros/12.5/system/"
        # Create symbolic links to files needed to run astros
        files = ["astros.exe", # Executable
                 "ASTRO.D01",  # *.DO1 file
                 "ASTRO.IDX"]  # *.IDX file
        for file in files:
            if not os.path.isfile(file):
                os.symlink(astrosInstallDir + file, file)

        command = "./astros.exe < " + projectName +  ".dat > " + projectName + ".out"
        logging.info("Running astros: "+command)
        os.system(command)
        logging.info("Completed running astros")

    elif nativeApp.lower() == "friction":
        frictionInstallDir = nativeLibDist
        if not os.path.isfile("friction.exe"):
            os.symlink(frictionInstallDir +"friction-sorcer.exe", "friction.exe")

        os.system("friction frictionInput.txt frictionOutput.txt > Info.out")
        logging.info("Completed running friction")

    os.chdir(cwd) # Move back to working directory


#
# Function for handling connections. This will be used to handle pyCAPS interaction
#
def pyCapsThread(conn, data, threadID):
    try:
        logging.info("Loading JSON....")
        jsonData = json.loads(data)

        projectName = jsonData['projectName']
        nativeApp = jsonData['nativeApp']
        csmData = jsonData['csmData']
        loadAIM = jsonData['aimData']
        analysisVals = jsonData['analysisVals']
        outputs = jsonData['outputs']

        myProblem = capsProblem()
        myGeometry = myProblem.loadCAPS(csmData)

        if('geometryVals' in jsonData):
            geometryVals = jsonData['geometryVals']
            for key, value in geometryVals.iteritems():
                if(isinstance(value, dict)):
                    myGeometry.setGeometryVal(key, value.items() )
                else:
                    myGeometry.setGeometryVal(key, value)

        currentDirectory = os.getcwd() # Get our current working directory

        if "altName" in loadAIM:
            altName = loadAIM["altName"]
        else:
            altName = loadAIM['aim']


        myAnalysis = myProblem.loadAIM(aim = loadAIM['aim'],
                                       altName = altName,
                                       analysisDir = currentDirectory+"/build/"+projectName+"-"+threadID,
                                       capsIntent = loadAIM['capsIntent'])

        for analysisVal in analysisVals:
            name = analysisVal['name']
            value = analysisVal['value']
            units = None
            if('units' in analysisVal):
                units = analysisVal['units']
            if(isinstance(value, dict)):
                myAnalysis.setAnalysisVal(name, value.items(), units )
            else:
                myAnalysis.setAnalysisVal(name, value, units)

        myAnalysis.aimPreAnalysis()
        nativeAppRunner(nativeApp, myAnalysis.analysisDir, projectName)
        myAnalysis.aimPostAnalysis()

        outVals = {}
        for output in outputs:
            outVals[output] = myAnalysis.getAnalysisOutVal(output)

        reply = json.dumps(outVals)
        conn.send(reply+"\n")

    except ValueError as err:
        logging.error('Decoding JSON has failed: ', str(err))
        conn.send("FAILED, "+str(err)+"\n")

    except Exception as err:
        logging.error('Exception caught: ', str(err))
        conn.send("FAILED, "+str(err)+"\n")

    conn.close()

#
# Main loop for receiving client requests
#
while 1:
    # Wait to accept a connection - blocking call
    conn, addr = serverSocket.accept()
    threadID = str(addr[0] + ':' + str(addr[1]))
    logging.info('Connected with ' + threadID)

    data = recvall(conn)
    if data == 'shutdown':
        logging.info("Client request to shutdown")
        break

    if data:
        # Start new pyCapsThread. Takes 1st argument as a function name to be run,
        # second is the tuple of arguments to the function.
        start_new_thread(pyCapsThread ,(conn, data, threadID))

serverSocket.close()