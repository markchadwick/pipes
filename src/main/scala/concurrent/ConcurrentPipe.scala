package pipes.concurrent

import java.lang.Runnable
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Future

import pipes.Pipe

object ConcurrentPipe {
  def apply[A, B](pipe: Pipe[A, B]): ConcurrentPipe[A, B] = 
    new ConcurrentPipe[A, B] {
      def processEntry(in: A) = pipe.apply(in)
      override def toString = pipe.toString
    }
}

trait ConcurrentPipe[In, Out] extends Pipe[In, Out] {
  def processEntry(in: In): Traversable[Out]
  def maxQueue = 10

  def put(in: In): Unit /* Future[Option[Out]] */ = workQueue.put(Some(in))
  def put(): Unit = workQueue.put(None)

  override def apply(in: In): Traversable[Out] = {
    put(in)
    put()

    Stream.continually(resultQueue.take)
          .takeWhile(_ != None)
          .map(_.get)
  }

  override def |[A](other: Pipe[Out, A]): ConcurrentPipe[In, A] = {
    other match {
      case cp: ConcurrentPipe[Out, A] ⇒ this.append(cp)
      case sp: Pipe[Out, A] ⇒ this.append(ConcurrentPipe(sp))
    }
  }

  private def append[A](pipe: ConcurrentPipe[Out, A]): ConcurrentPipe[In, A] = {
    var me = this
    new ConcurrentPipe[In, A] {
      def processEntry(in: In) = {
        val result = me.processEntry(in)
        result.flatMap(pipe.apply)
      }
    }
  }

  private val workQueue = new ArrayBlockingQueue[Option[In]](maxQueue)
  private val resultQueue = new ArrayBlockingQueue[Option[Out]](maxQueue)
  private var running = true
  private val workThread = {
    val thread = new Thread(new Runnable {
      def run() = while(running) {
        workQueue.take.map(processEntry) match {
          case None ⇒ resultQueue.put(None)
          case Some(xs) ⇒ xs.foreach(r ⇒ resultQueue.put(Some(r)))
        }
      }
    })
    thread.setDaemon(true)
    thread.setName(toString)
    thread
  }
  workThread.start()
}
