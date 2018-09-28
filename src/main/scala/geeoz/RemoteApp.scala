/*
 * Copyright © 2015 Geeoz, and/or its affiliates. All rights reserved.
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

package geeoz

import java.io.IOException

import sbt._

import scala.util.{Failure, Success, Try}

/** The core part of solution - shared object.
  */
class RemoteApp(cls: String) {
  /** Class path from main build definition.
    */
  var cp: Seq[File] = Nil
  /** Logs in same style as the build logs.
    */
  var log: Option[Logger] = None
  /** Remote app process instance.
    */
  var proc: Option[scala.sys.process.Process] = None
  /** Host of the remote app.
    */
  var host = "127.0.0.1"
  /** Port of the remote app.
    */
  var port = "8081"
  /** Max attempts to connect to the app.
    */
  var attempts = 20
  /** Properties for the fork process.
    */
  var props = Map.empty[String, String]

  /** Setup remote app.
    * @param classpath class path from main build definition
    * @param logger logs in same style as the build logs
    * @param appHost host of the remote app
    * @param appPort port of the remote app
    * @param maxAttempts max attempts to connect to the app
    * @param forkProps for the fork process
    */
  def setup(classpath: Seq[File],
            logger: Logger,
            appHost: String = "127.0.0.1",
            appPort: String = "8081",
            maxAttempts: Int = 20,
            forkProps: Map[String, String] = Map.empty[String, String]):
  RemoteApp = {
    cp = classpath
    log = Option(logger)
    host = appHost
    port = appPort
    attempts = maxAttempts
    props = forkProps
    this
  }

  /** Start RemoteApp in java fork.
    */
  def start(): Unit = {
    log.foreach(_.info(s"Starting $cls application ..."))
    val options = ForkOptions().withOutputStrategy(StdoutOutput)
    // build classpath string
    val cpStr = cp.map(_.getAbsolutePath).mkString(":")
    val properties = props map { case (key, value) => s"-D$key=$value" }
    val arguments: Seq[String] = List("-classpath", cpStr) ++ properties
    // Here goes the name of the class which would be launched
    val mainClass: String = cls
    // Launch it. Pay attention that class name comes last in the list of args
    proc = Option(Fork.java.fork(options, arguments :+ mainClass))

    // make sure application really started/failed before proceed to the tests
    waitForStart().recover({ case e =>
      stop()
      throw e
    }).get
  }

  /** Stop RemoteApp in java fork.
    */
  def stop(): Unit = {
    log.foreach(_.info(s"Stopping $cls application $proc ..."))
    // kill application
    proc.foreach(_.destroy())
    proc = None
  }

  /** Verify that application is started.
    * @return application status after 10 retries
    */
  private def waitForStart(): Try[_] = {
    val u = new URL(s"http://$host:$port")
    val c = u.openConnection()
    val result = (1 to attempts).toStream map { i =>
      log.foreach(_.info(s"Connection attempt $i of $attempts"))
      Try {
        c.connect()
      }
    } find {
      case Success(_) => true
      case Failure(e: IOException) => Thread.sleep(1500); false
      case Failure(_) => false
    }
    if (result.isEmpty)
      Failure(new RuntimeException(
        s"Failed to connect to $cls application after $attempts attempts"))
    else
      Success(None)
  }
}

/** RemoteApp object for initialization simplification.
  */
object RemoteApp {
  /** Create RemoteApp instance with specified main class.
    * @param mainClass main class to run
    * @return an instance otf the RemoteApp
    */
  def apply(mainClass: String): RemoteApp = {
    new RemoteApp(mainClass)
  }
}
