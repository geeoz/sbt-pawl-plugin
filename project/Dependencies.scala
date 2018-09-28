/*
 * Copyright © 2018 Geeoz, and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Research Projects is dual-licensed under the GNU General Public
 * License, version 2.0 (GPLv2) and the Geeoz Commercial License.
 *
 * Solely for non-commercial purposes. A purpose is non-commercial only if
 * it is in no manner primarily intended for or directed toward commercial
 * advantage or private monetary compensation.
 *
 * This Geeoz Software is supplied to you by Geeoz in consideration of your
 * agreement to the following terms, and your use, installation, modification
 * or redistribution of this Geeoz Software constitutes acceptance of these
 * terms. If you do not agree with these terms, please do not use, install,
 * modify or redistribute this Geeoz Software.
 *
 * Neither the name, trademarks, service marks or logos of Geeoz may be used
 * to endorse or promote products derived from the Geeoz Software without
 * specific prior written permission from Geeoz.
 *
 * The Geeoz Software is provided by Geeoz on an "AS IS" basis. GEEOZ MAKES NO
 * WARRANTIES, EXPRESS  OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
 * WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, REGARDING THE GEEOZ SOFTWARE OR ITS USE AND OPERATION ALONE OR IN
 * COMBINATION WITH YOUR PRODUCTS.
 *
 * IN NO EVENT SHALL GEEOZ BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION
 * AND/OR DISTRIBUTION OF THE GEEOZ SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER
 * THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY OR
 * OTHERWISE, EVEN IF GEEOZ HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * A copy of the GNU General Public License is included in the distribution in
 * the file LICENSE and at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.html
 *
 * If you are using the Research Projects for commercial purposes, we
 * encourage you to visit
 *
 *     http://products.geeoz.com/license
 *
 * for more details.
 *
 * This software or hardware and documentation may provide access to
 * or information on content, products, and services from third parties.
 * Geeoz and its affiliates are not responsible for and expressly disclaim
 * all warranties of any kind with respect to third-party content, products,
 * and services. Geeoz and its affiliates will not be responsible for any loss,
 * costs, or damages incurred due to your access to or use of third-party
 * content, products, or services. If a third-party content exists, the
 * additional copyright notices and license terms applicable to portions of the
 * software are set forth in the THIRD_PARTY_LICENSE_README file.
 *
 * Please contact Geeoz or visit www.geeoz.com if you need additional
 * information or have any questions.
 */

import sbt.Keys._
import sbt._

object Dependencies {
  val Versions = Seq(
    scalaVersion := "2.12.4"
  )

  // A library to code Scala programmatically
  val treehuggerVersion             = "0.4.3"     // MIT
  // Maven Frontend Plugin Core
  val frontendPluginCoreVersion     = "0.0.23"    // ApacheV2
  // This project provides the building blocks for web oriented sbt plugins.
  val sbtWebVersion                 = "1.4.3"     // ApacheV2
  // Simple Logging Facade for Java (SLF4J)
  val slf4jVersion                  = "1.7.12"    // MIT
  // PMD is a source code analyzer. It finds common programming flaws like unused variables, empty catch blocks, unnecessary object creation, and so forth.
  val pmdVersion                    = "5.3.3"     // BSD-style
  // ModeShape is a JCR repository implementation with support for federation and sequencing.
  val jcrVersion                    = "2.0"
  val modeshapeVersion              = "4.5.0.Final"   // ApacheV2

  object Compile {
    val treehugger            = "com.eed3si9n" %% "treehugger" % treehuggerVersion
    val frontendPluginCore    = "com.github.eirslett" % "frontend-plugin-core" % frontendPluginCoreVersion
    val slf4j                 = "org.slf4j" % "slf4j-log4j12" % slf4jVersion
    val pmdDist               = "net.sourceforge.pmd" % "pmd-dist" % pmdVersion

    val jcr                   = "javax.jcr" % "jcr" % jcrVersion
    val modeshapeCommon       = "org.modeshape" % "modeshape-common" % modeshapeVersion
    val modeshapeJcr          = "org.modeshape" % "modeshape-jcr" % modeshapeVersion
    val modeshapeSchematic    = "org.modeshape" % "modeshape-schematic" % modeshapeVersion
    val jcrSeq                = Seq(jcr, modeshapeCommon, modeshapeJcr, modeshapeSchematic)
  }

  import Compile._

  val l = libraryDependencies

  // Dependencies for sbt-frontend plugin
  val frontend = l ++= Seq(frontendPluginCore, slf4j)

  // Dependencies for sbt-atom plugin
  val atom = l ++= Seq(treehugger) ++ jcrSeq

  // Dependencies for sbt-pawl plugin
  val pawl = l ++= Seq(treehugger)

  // Dependencies for sbt-pmd plugin
  val pmd = l ++= Seq(pmdDist)
}