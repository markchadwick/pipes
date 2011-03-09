package jgrep.io

import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer

import pipes.Pipe

/*
class LineReader extends Pipe[ByteBuffer, String] {
  def apply(s: ByteBuffer) = Nil
  override def newQueue[A](size: Int) = new java.util.concurrent.LinkedBlockingQueue[A]()
  private val EOL = '\n'.toByte
  private var buf = new StringBuilder()

  var first = true
  override def process(in: ByteBuffer, put: String ⇒ Unit) {
    while(in.remaining > 1) {
      val next = in.get()
      if(next == EOL) {
        // println("[LineReader] FOUND A LINE!")
        put(buf.toString)
        buf = new StringBuilder()
      } else {
        buf.append(next)
      }
    }
    first = false
  }

  override def drain = {
    val res = if(buf.length > 0) List(buf.toString) else Nil
    buf = new StringBuilder()
    res
  }

  override def toString = "LineReader"
}
*/
