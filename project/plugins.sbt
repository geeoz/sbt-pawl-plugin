
// Visualize your project's dependencies.
// sbt dependency-graph - shows an ASCII graph of the project's dependencies on the sbt console
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

// -- RELEASE --
// A sbt plugin for publishing your project to the Maven central repository through the REST API of Sonatype Nexus.
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")

// The sbt-pgp plugin provides PGP signing for SBT.
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.1")