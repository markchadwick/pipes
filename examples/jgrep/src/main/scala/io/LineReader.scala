package jgrep.io

import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset

import pipes.Pipe

class LineReader extends Pipe[ByteBuffer, String] {
  val charset = Charset.forName("UTF-8")
  val decoder = charset.newDecoder

  def apply(s: ByteBuffer) = Nil
  def newQueue[A] = new java.util.concurrent.LinkedBlockingQueue[A]()

  private var buf = new StringBuilder()
  private var lastPut: Option[String ⇒ Unit] = None

  override def process(in: ByteBuffer, put: String ⇒ Unit) {
    lastPut = Some(put)
    val charBuffer = decoder.decode(in)
    println("[LineReader]: Read %s".format(charBuffer.remaining))
    println("[LineReader]: %s jimmies on the queue".format(inputQueue.size))
    while(charBuffer.remaining > 1) {
      val next = charBuffer.get()
      if(next == '\n') {
        put(buf.toString)
        buf = new StringBuilder()
      } else {
        buf.append(next)
      }
    }
  }

  override def drain = {
    val res = if(buf.length > 0) List(buf.toString) else Nil
    buf = new StringBuilder()
    res
  }

  override def toString = "LineReader"
}
