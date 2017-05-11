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

import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
/**
 *
 * @author Dennis Reedy
 */
class FrictionTester {
    @Test
    void testFriction() {
        def options = ["projectName"    : "FrictionAnalysisTest",
                       "projectDataRoot": "${System.getProperty("projectDataRoot")}",
                       "projectDir"     : "${System.getProperty("projectDir")}"]
        FrictionConfig friction = new FrictionConfig(options)
        PyCAPSManager pyCAPSManager = new PyCAPSManager().launch()
        File json = new File("${System.getProperty("projectDir")}/frictionJson.txt")
        json.text = friction.get()
        def result = pyCAPSManager.getPyCAPS().submit(friction.get())
        assertNotNull(result)

        def formDrag = result['CDform']
        def totalDrag = result['CDtotal']
        def frictionDrag = result['CDfric']

        assertTrue(formDrag[0]==0.00331)
        assertTrue(formDrag[1]==0.00308)

        assertTrue(totalDrag[0]==0.01321)
        assertTrue(totalDrag[1]==0.01227)

        assertTrue(frictionDrag[0]==0.0099)
        assertTrue(frictionDrag[1]==0.00919)

        //Total drag = [0.01321, 0.01227]
        //Form drag = [0.00331, 0.00308]
        //Friction drag = [0.0099, 0.00919]

        /*result.each { k, v ->
            println "$k: $v"
        }*/
    }
}
