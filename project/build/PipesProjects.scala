import sbt._
import reaktor.scct.ScctProject

class PipesProject(info: ProjectInfo) extends ParentProject(info) {

  val core = project("pipes-core", "pipes-core", new PipesCore(_))

  class PipesCore(info: ProjectInfo) extends DefaultProject(info)
                                     with ScctProject {
    DependsOn.scalatest
  }

  // //////////////////////////////////////////////////////////////////////////
  // Examples

  val jgrep = project("examples" / "jgrep", "jgrep", new JGrep(_), core)

  class JGrep(info: ProjectInfo) extends DefaultProject(info) {
    DependsOn.scalatest
  }

  object DependsOn {
    lazy val scalatest = "org.scalatest" % "scalatest" % "1.3" % "test"
  }
}

