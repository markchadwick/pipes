package jgrep.io

import java.util.Date
import java.io.File
import scala.io.Source

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import pipes.Pipe

class ReaderSpec extends FlatSpec with ShouldMatchers {
  behavior of "Reader Pipeline"

  it should "be faster than scala.io.Source" in {
    val fileName = "/home/mchadwick/log.small.json"

    readLines("Scala Reader", fileName, scalaReader)
    readLines("Pipe Reader", fileName, pipeReader)
  }

  private def readLines(name: String, fileName: String, pipe: Pipe[String, String]) {
    val size = timed(name) {
      pipe.apply(fileName).size
    }
    println("[%s] Read %s lines".format(name, size))
  }

  private def pipeReader = 
    new FileChunkReader(1024 * 64) | new LineReader()

  private def scalaReader = new Pipe[String, String] {
    def apply(fileName: String) = {
      val file = new File(fileName)
      Source.fromFile(file).getLines.toTraversable
    }
  }

  private def timed[A](name: String)(func: ⇒ A): A = {
    val start = new Date().getTime
    val result = func
    val seconds = (new Date().getTime - start).toFloat / 1000.0
    println("[%s] took %.03fs".format(name, seconds))
    return result
  }
}