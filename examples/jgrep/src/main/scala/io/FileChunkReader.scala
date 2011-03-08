package jgrep.io

import java.nio.ByteBuffer
import java.io.File
import java.io.FileInputStream

import pipes.Pipe

class FileChunkReader(bufSize: Int) extends Pipe[String, ByteBuffer] {
  def apply(s: String) = Nil

  def newBuffer = ByteBuffer.allocateDirect(bufSize)

  override def process(in: String, put: ByteBuffer ⇒ Unit) {
    val fileStream = new FileInputStream(new File(in))
    val channel = fileStream.getChannel

    while(channel.position < channel.size) {
      val buf = newBuffer
      channel.read(buf)
      buf.flip
      println("[FileChunkReader]: put %s".format(buf.remaining))
      put(buf)
    }
    fileStream.close()
  }

  override def newThread(r: Runnable) = {
    val thread = super.newThread(r)
    thread.setName("FileChunkReader")
    thread.setPriority(Thread.MAX_PRIORITY)
    thread
  }

  override def toString = "FileChunkReader"
}
