package pipes

import pipes.processor.ThreadProcessor


trait Pipe[In, Out] extends ThreadProcessor[In, Out] {
  def apply(in: In): Traversable[Out]

  def process(in: In, put: Out ⇒ Unit) = apply(in).foreach(put)

  override def newQueue[A](size: Int) =
    new java.util.concurrent.LinkedBlockingQueue[A]()

  def |[A](sink: Pipe[Out, A]): Pipe[In, A] = {
    val source = this
    new Pipe[In, A] {
      override def enqueue(in: In) = source.enqueue(in)

      def apply(in: In): Traversable[A] = run {
        source.enqueue(in)
      }

      override def runWithPut[T](put: A ⇒ Unit)(func: ⇒ T): Traversable[A] = {
        sink.runWithPut(put) { source.runWithPut(sink.enqueue _)(func) }
        outputQueue.put(None)
        Stream.continually(outputQueue.take())
              .takeWhile(_ != None)
              .map(_.get)
      }

      override def toString = "(%s ⇒ %s)".format(source, sink)
    }
  }

  def |(other: Option[Pipe[Out, Out]]): Pipe[In, Out] =
    other match {
      case None ⇒ this
      case Some(pipe) ⇒ this | pipe
    }
}
