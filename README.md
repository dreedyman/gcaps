# gcaps
Groovy/Java wrapper for CAPS (Computational Aircraft Prototype Syntheses)

This is an experimental project providing a Groovy/Java wrapper around the MIT CAPS (Computational Aircraft Prototype Syntheses) project. It parallels/aligns the work done by Dr. Ryan Durscher's contribution of pyCAPS (A python wrapper for CAPS that uses Cython).

The project assumes that you have downloaded the EngSketchPad, along with OpenCASCADE, and have that built, with the requiste environment variables sourced (`ESPenv.sh`). You will also need the MSTC Engineering `native-lib-dist-open-6.2` distribution in order to have access to the Engineering applications.

The project currently uses JNA to wrap the underlying CAPS APIs. There has been some success, but recent updates to ESP have resulted in an unstable project. Further investigation ensues...

Build the project using `gradlew` (gradle wrapper)

There are stand-alone scripts in the src/main/scripts directory that can be run. Run it as follows:

```
groovy -Djna.library.path=$ESP_ROOT/lib \
       -Dnative.lib.dist=../distributions/native-lib-dist-open-6.2/mac \
       src/main/scriptsn/AstrosAGARD445.groovy
```        

Note that when running the script, you will be prompted as follows:

`Press enter key to proceed (you have up to 60 seconds).`

This allows you to attach a debugger (lldb or gdb) to the process in order to help determine where the EXC_BAD_ACCESS is coming from.

There are JUnit tests in the src/tests directory, they currently fail.
