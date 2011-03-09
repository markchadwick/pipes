package jgrep.io

import java.nio.ByteBuffer
import java.io.File
import java.io.FileInputStream
import java.util.Arrays.copyOfRange

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
      put(buf)
    }
    fileStream.close()
  }

  override def newThread(r: Runnable) = {
    val thread = super.newThread(r)
    thread.setPriority(Thread.MAX_PRIORITY)
    thread
  }

  override def toString = "FileChunkReader"
}


class LineChunker extends Pipe[ByteBuffer, Array[Byte]] {
  var saved = new Array[Byte](0)
  val EOL = '\n'.toByte

  def apply(in: ByteBuffer) = Nil

  override def process(block: ByteBuffer, put: Array[Byte] ⇒ Unit) {
    block.flip

    var chunkSize = block.remaining + saved.length
    var chunkEnd = chunkSize
    val chunk = new Array[Byte](chunkSize)

    System.arraycopy(saved, 0, chunk, 0, saved.length)
    block.get(chunk, saved.length, block.remaining)

    val lastEol = trim(chunk, 0, chunkSize) + 1
    val last = (saved.length max lastEol) min chunkSize
    saved = copyOfRange(chunk, last, chunkEnd)

    val result = new Array[Byte](last)
    System.arraycopy(chunk, 0, result, 0, last)
    put(result)
  }

  def trim(chunk: Array[Byte], end: Int, start: Int) = {
    var i = end - 1
    while(i > start && chunk(i) != EOL) i -= 1
    i
  }

  override def newThread(r: Runnable) = {
    val thread = super.newThread(r)
    thread.setPriority(Thread.MAX_PRIORITY)
    thread
  }

  override def toString = "FileChunker"
}

class LineReader extends Pipe[Array[Byte], String] {
  def apply(s: Array[Byte]) = {
    new String(s, "UTF-8").split("\n")
  }
}
