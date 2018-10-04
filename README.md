# Sbt PAWL plugin
Sbt plugin generates all necessary utility classes for PAWL Framework.

## Building

This will build the plugin, publish it to a local artifactory repository, then resolve the dependencies of the project. 

### Requirements

- [Java JDK (1.8+)] [java]
- [SBT (1.2.1+)] [sbt]

### Steps

1. Download the source code from git: `git clone git@github.com:geeoz/sbt-pawl-plugin.git`
2. Open a command line in the cloned directory: `cd ./sbt-pawl-plugin`
3. Now run the sbt to build the plugin: `sbt publishLocal`

### Configuration
  - `build.sbt` - sbt file with task definitions
  - `project/build.properties` - property file with SBT configuration
  - `project/Dependencies.scala` - scala file with module dependencies for the project
  - `project/plugin.sbt` - sbt file with plugin configurations

## More Help

More assistance can be found in our documentation and our [developer hub] [dev-hub].

[dev-hub]: http://developer.geeoz.com "Geeoz Developer Hub"
[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html "Java"
[sbt]: http://www.scala-sbt.org "SBT"

