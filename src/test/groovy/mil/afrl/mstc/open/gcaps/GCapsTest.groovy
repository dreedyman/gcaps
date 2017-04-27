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


/**
 *
 * @author Dennis Reedy
 */
class GCapsTest {
    @Test
    void testAstros() {
        def options = ["projectName"    : "AstrosModalAGARD445",
                       "projectDataRoot": "${System.getProperty("projectDataRoot")}",
                       "projectDir"     :  "${System.getProperty("projectDir")}"]

        AstrosAGARD445 astrosAGARD445 = new AstrosAGARD445()
        astrosAGARD445.init(options)
        def naturalFreq = astrosAGARD445.result()
        assertNotNull(naturalFreq)
        int mode = 1
        naturalFreq.each { n ->
            println(String.format("Natural freq (Mode %d) = %s (Hz)", mode, n))
            mode += 1
        }
    }

    //@Test
    void testFriction() {
        def options = ["projectName"    : "FrictionAnalysisTest",
                       "projectDataRoot": "${System.getProperty("projectDataRoot")}",
                       "projectDir"     :  "${System.getProperty("projectDir")}"]
        Friction friction = new Friction()
        friction.init(options)
        def outputs = friction.result()
        assertNotNull(outputs)
        outputs.each { k, v ->
            println "$k: $v"
        }
    }

}
