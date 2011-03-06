import sbt._
import reaktor.scct.ScctProject

class PipesProject(info: ProjectInfo) extends DefaultProject(info)
                                      with ScctProject {
  lazy val scalatest = "org.scalatest" % "scalatest" % "1.3" % "test"
}
