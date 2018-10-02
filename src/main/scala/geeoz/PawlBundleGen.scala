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

package geeoz

import java.io.File
import java.util.{Locale, ResourceBundle}

import sbt._
import treehugger.forest._
import treehuggerDSL._

import scala.collection.convert.ImplicitConversionsToScala._


/** This class generates all necessary utility classes for PAWL Framework.
  */
object PawlBundleGen {
  def genBundle(resDir: File,
                genDir: File,
                loader: ClassLoader,
                pack: String)(implicit log: Logger): Seq[File] = {
    if (!resDir.canRead) {
      return Nil
    }
    log.debug("Creating directory " + genDir)
    IO.createDirectory(genDir)

    implicit val dir: File = resDir
    val resources = List(
      gen("bundle", loader, pack)).flatten

    if (resources isEmpty) {
      Seq()
    } else {
      resources.flatMap(_.map(entry => {
          val file =
            genDir / s"${pack.replaceAll("\\.", File.separator)}/${entry._1}.scala"
          log.info("Generating " + file.getPath)
          IO.write(file, treeToString(entry._2))
          file
        }).toSeq
      )
    }
  }

  /** Create Tree for resources in corresponded directory.
    * @param res resource folder to process
    * @param loader class loader to use
    * @param pack class package name
    * @param resDir full path to project resources directory
    * @return wrapped Tree or None if folder doesn't exists
    */
  private def gen(res: String, loader: ClassLoader, pack: String)
                 (implicit resDir: File): Option[Map[String, Tree]] = {
    val dir = resDir / res
    if (dir.exists() && dir.isDirectory) {
      val map = IO.listFiles(dir)
        .filterNot(file => file.name.contains("_"))
        .map(file => {
          val name =
            file.base.split("-").map(s => s.capitalize).mkString + res.capitalize
          val tree = (TRAITDEF(name) := BLOCK(
            ResourceBundle.getBundle(s"$res/${file.base}", Locale.US, loader)
              .keySet().map(key => {
              val const = key.replaceAll("\\.", "_")
              (LAZYVAL(const)
                := TUPLE(LIT(s"$res/${file.base}"), LIT(key))) withDoc
                s"Generated constant for $res/${file.base}@$key.": Tree
            })
          )) withDoc s"Generated constants for $res path '$res/${file.name}'.": Tree
          (name, BLOCK(tree) inPackage pack)
        }).toMap[String, Tree]
      Some(map)
    } else {
      None
    }
  }
}
