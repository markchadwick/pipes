import sbt._
import reaktor.scct.ScctProject

class PipesProject(info: ProjectInfo) extends ParentProject(info) {

  val core = project("pipes-core", "pipes-core", new PipesCore(_))

  class PipesCore(info: ProjectInfo) extends DefaultProject(info)
                                     with ScctProject {
    lazy val scalatest = "org.scalatest" % "scalatest" % "1.3" % "test"
  }

  // //////////////////////////////////////////////////////////////////////////
  // Examples

  // val jgrep = project("examples" / "jgrep", "jgrep", new JGrep(_), core)

  class JGrep(info: ProjectInfo) extends DefaultProject(info) {
    // DependsOn.scalatest

    lazy val jackson = jacksonDependency("jackson-core-asl")
    lazy val jacksonMapper = jacksonDependency("jackson-mapper-asl")
    lazy val jacksonSmile = jacksonDependency("jackson-smile")
    lazy val scalatest = "org.scalatest" % "scalatest" % "1.3" % "test"

    private def jacksonDependency(name: String) =
      "org.codehaus.jackson" % name % "1.6.4"
  }
}

