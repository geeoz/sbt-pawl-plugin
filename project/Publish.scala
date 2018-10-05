/*
 * Copyright 2015 Geeoz Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.typesafe.sbt.SbtPgp.autoImport._
import sbt.Keys._
import sbt._

object Publish {
  lazy val settings = Seq(
    resolvers := {
      val localMaven = Resolver.mavenLocal
      localMaven +: resolvers.value
    },
    credentials += Credentials(
      "Sonatype Nexus Repository Manager",
      "oss.sonatype.org",
      sys.env.getOrElse("SONATYPE_USER", ""),
      sys.env.getOrElse("SONATYPE_KEY", "")),
    useGpg := true,
    publishArtifact in Test := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra :=
      <url>https://developer.geeoz.com</url>

        <licenses>
          <license>
            <name>
              Dual-licensed under the GNU General Public License, version
              2.0 (GPLv2) and the Geeoz Commercial License.
            </name>
            <url>http://products.geeoz.com/license</url>
            <distribution>repo</distribution>
          </license>
        </licenses>

        <developers>
          <developer>
            <id>alexander.voloshyn</id>
            <name>Alex Voloshyn</name>
            <email>alex@geeoz.com</email>
            <organization>Geeoz Software</organization>
            <organizationUrl>http://www.geeoz.com</organizationUrl>
            <roles>
              <role>Architect</role>
              <role>Developer</role>
            </roles>
            <timezone>+2</timezone>
          </developer>
        </developers>

        <scm>
          <connection>scm:git:ssh://github.com:geeoz/sbt-pawl-plugin.git</connection>
          <developerConnection>scm:git:ssh://github.com:geeoz/sbt-pawl-plugin.git</developerConnection>
          <url>https://github.com/geeoz/sbt-pawl-plugin</url>
        </scm>
  )
}
