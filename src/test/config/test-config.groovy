/*
 * Configuration file for integration test cases
 */
PyCAPSManagerTest {
    groups = System.getProperty("user.name")
    numCybernodes = 1
    numMonitors = 1
    opstring = 'deploy/pyCapsDeploy.groovy'
    autoDeploy = true
}