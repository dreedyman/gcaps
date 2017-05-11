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
/**
 *
 * @author Dennis Reedy
 */
class AstrosTester {

    @Test
    void test() {
        //PyCAPSManager pyCAPSManager = new PyCAPSManager().launch()
        PyCAPSManager pyCAPSManager = new PyCAPSManager()
        AstrosConfig astros = new AstrosConfig(System.getProperty("projectDataRoot"),
                                               System.getProperty("projectDir"),
                                               "AstrosModalAGARD445")
        PyCAPS pyCAPS = pyCAPSManager.getPyCAPS()
        File json = new File("${System.getProperty("projectDir")}/astrosJson.txt")
        json.text = astros.get()
        def result = pyCAPS.submit(astros.get())
        int mode = 1
        result['EigenFrequency'].each { eV ->
            println(String.format("Natural freq (Mode %d) = %s (Hz)", mode, eV))
            mode++
        }
        pyCAPS.shutdown()
    }


}
