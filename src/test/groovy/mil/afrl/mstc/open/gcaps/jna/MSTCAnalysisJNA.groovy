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
package mil.afrl.mstc.open.gcaps.jna

/**
 *
 * @author Dennis Reedy
 */
abstract class MSTCAnalysisJNA {
    String projectName
    String projectDir
    String projectDataRoot
    GCaps analysis = new GCaps()

    def init(options) {
        projectName = options["projectName"]
        projectDataRoot = options['projectDataRoot']
        projectDir = options['projectDir']
    }

    abstract def result()

    def close() {
        analysis.close()
    }
}