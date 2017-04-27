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

import java.nio.file.Files
import java.util.concurrent.TimeUnit

/**
 *
 * @author Dennis Reedy
 */
class OS {
    static symlink(File source, File target) {
        Files.createSymbolicLink(target.toPath(), source.toPath())
    }

    static exec(String command, File workingDir) {
        def commandArray = addShellPrefix(command)
        println commandArray
        def process = new ProcessBuilder(commandArray)
                .directory(workingDir)
                .redirectErrorStream(true)
                .start()
        process.inputStream.eachLine {println it}
        process.waitFor()
        return process.exitValue()
    }

    static void waitOnInput(long seconds) {
        Thread reader = new Thread(new Runnable() {
            @Override
            void run() {
                println  "Press enter key to proceed (you have up to $seconds seconds)."
                System.in.read()
            }
        })

        reader.start()
        reader.join(TimeUnit.SECONDS.toMillis(seconds))
    }

    private static addShellPrefix(String command) {
        def commandArray = new String[3]
        commandArray[0] = "sh"
        commandArray[1] = "-c"
        commandArray[2] = command
        return commandArray
    }

}
