package pipes.concurrent

import java.util.concurrent.FutureTask
import java.util.concurrent.Future
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

import pipes.Pipe

object ConcurrentPipe {
  def apply[A, B](pipe: Pipe[A, B]): ConcurrentPipe[A, B] = 
    new ConcurrentPipe[A, B] {
      def processEntry(in: A) = pipe.apply(in)
      override def toString = pipe.toString
    }

  def threadFactory(name: String) = new ThreadFactory {
    def newThread(r: Runnable) = {
      val thread = new Thread(r)
      thread.setName("ConcurrentPipe [%s]".format(name))
      thread.setDaemon(true)
      thread
    }
  }
}

trait ConcurrentPipe[In, Out] extends Pipe[In, Out] {
  def processEntry(in: In): Traversable[Out]

  def getExecutorService = Executors.newSingleThreadExecutor(
                              ConcurrentPipe.threadFactory(toString))
  lazy val executor = getExecutorService

  def put(in: In): Future[Traversable[Out]] = {
    val future = executor.submit(callable(in))
    future
  }


  def |[A](other: ConcurrentPipe[Out, A]): ConcurrentPipe[In, A] = {
    val me = this
    new ConcurrentPipe[In, A] {
      def processEntry(in: In): Traversable[A] = {
        me.processEntry(in).map(other.put).flatMap(_.get)
      }
      override def toString = "(%s ⇒ %s)".format(me.toString, other.toString)
    }
  }

  override def apply(in: In): Traversable[Out] = {
    put(in).get
  }

  private def callable(in: In) = new Callable[Traversable[Out]] {
    def call() = processEntry(in)
  }
}
