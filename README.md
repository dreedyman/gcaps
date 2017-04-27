# gcaps
Groovy/Java wrapper for CAPS (Computational Aircraft Prototype Syntheses)

This is an experimental project providing a Java/Groovy wrapper around the MIT CAPS (Computational Aircraft Prototype Syntheses) project. It parallels/aligns the work done by Dr. Ryan Durscher's contribution of pyCAPS (A python wrapper for CAPS that uses Cython).

The project assumes that you have downloaded the EngSketchPad, along with OpenCASCADE, and have that built, with the requiste environment variables sourced (ESPenv.sh). You will also need the MSTC Engineering native-lib-dist-open-6.2 distribution in order to have access to the Engineering applications.

The project currently uses JNA to wrap the underlying CAPS APIs. There has been some success, but recent updates to ESP have resulted in an unstable project. Further investigation ensues...
