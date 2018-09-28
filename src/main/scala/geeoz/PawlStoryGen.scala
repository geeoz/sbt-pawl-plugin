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

import java.io.File

import sbt._
import treehugger.forest._
import treehuggerDSL._

/** This class generates all necessary utility classes for PAWL Framework.
  */
object PawlStoryGen {
  def genRunners(storyDir: File,
                 outDir: File)(implicit log: Logger): Seq[File] = {
    val ext = "IT"
    val extendPack = "pawl.jbehave"
    val extend = "AbstractWebStory"

    if (!storyDir.canRead) {
      return Nil
    }

    var files = Seq[File]()
    deepListFiles(storyDir, """.*\.story""".r).foreach(file => {
      val story = file.relativeTo(storyDir).get
      val name = story.base.split('_').map(_.capitalize).mkString + ext
      val parent = story.getParent
      val imp = IMPORT(extendPack, extend)
      val runClassDef = (CLASSDEF(name) withParents extend: Tree
        ) withDoc "Generated PAWL Framework story runner class."
      val runSpec = outDir / parent / s"$name.scala"
      val p = BLOCK(imp, runClassDef) inPackage parent.replaceAll(File.separator, ".")
      log.info("Generating " + runSpec.getPath)
      IO.write(runSpec, treeToString(p))
      files = files :+ runSpec
    })
    files
  }

  import scala.util.matching.Regex

  /** Retrieve file list including sub-folders.
    * @param f root directory
    * @param r filter for files
    * @return file array
    */
  private def deepListFiles(f: File, r: Regex): Array[File] = {
    val these = f.listFiles
    val good = these.filter(f => r.findFirstIn(f.getName).isDefined)
    good ++ these.filter(_.isDirectory).flatMap(deepListFiles(_, r))
  }
}
