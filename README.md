# Capstone2024-2025 KSL Web App

Dependencies:
  - Gradle 8.13
  - JVM jdk-17 //For KSL library compatibility
  - Javalin, Thymleaf, and KSL library dependencies can be found in build.gradle.kts
  - KSL model jar named Model1 (See below explanation starting on line 23)
  - Web hosting server of some kind (currently we are using an EC2 instance on AWS)
    + NOTE: the program can be ran locally and binds to port 80 on localhost
    + NOTE: program is designed to bind to port 80 on any web hosting service

How To Build and Execute:
  - build.gradle.kts has comments by code for different build outputs
  - Compile and Execute (No Jar Output):
    + For a faster build comment out the shadowJar dependencies labeled in the build.gradle.kts
    + Edit application node in the build.gradle.kts to point to your desired file
    + Open the command line in the same directory as the build.gradle.kts and enter "gradle build" to compile the program // it is normal for the first compilation to take a while longer
    + To execute in the same directory run "gradle run"
  - Compile the Program into a Jar and Execute using shadowJar:
    + Make sure the labeled shadowJar dependencies are not commented out in the build.gradle.kts
    + Edit application node in the build.gradle.kts to point to your desired file
    + Open the command line in the same directory as the build.gradle.kts and enter ".\gradlew shadowJar"
    + After your program is finished building you will find the associated .jar in the build\libs folder (relative to the build.gradle.kts) with the hyphenated name MyApp-1.0.jar or whatever name you chose in your shadowJar.
    + To run your .jar navigate to the directory it is stored using the command line and enter "java -jar {Your jar name here}.jar"

How to Build a Model jar and Related Architecture:
  - Follow the How to build and Execute using shadowJar section
  - For a prebuilt compatible model turn the StemFairMixerWithEnhancedMovement.kt into a .jar named Model1.jar
  - Models built into a .jar manually must be placed in the simulation folder src\main\kotlin\simulation (relative to the build.gradle.kts)
  - Uploaded models are automatically placed in the simulation folder
  - A jar with the name "Model1.jar" must be placed into the simulation folder for the existing model section of the webapp to execute properly (this is an example model).
