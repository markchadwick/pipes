package pipes

import pipes.processor.ThreadProcessor


trait Pipe[In, Out] { /* extends ThreadProcessor[In, Out] { */
  def apply(in: In): Traversable[Out]

  def process(in: In, put: Out ⇒ Unit) = {
    apply(in).foreach(put)
  }

  def |[A](snk: Pipe[Out, A]): Pipe[In, A] = {
    val src = this
    new CombinedPipe[In, Out, A] {
      val source = src
      val sink = snk
    }
  }

  def |(other: Option[Pipe[Out, Out]]): Pipe[In, Out] = {
    other match {
      case None ⇒ this
      case Some(pipe) ⇒ this | pipe
    }
  }
}

protected trait CombinedPipe[In, A, Out] extends Pipe[In, Out]
                                         with ThreadProcessor[In, A, Out] {
  val source: Pipe[In, A]
  val sink: Pipe[A, Out]

  // override def enqueue(in: In) = source.enqueue(in)

  def apply(in: In): Traversable[Out] = run {
    enqueue(in)
  }
    
  override def defaultPut(out: A) = sink.enqueue(out)

  override def runWithPut[T](put: A ⇒ Unit)(func: ⇒ T): Traversable[Out] = {
    sink.run {
      source.runWithPut(put)(func)
    }
  }

  override def toString = "(%s ⇒ %s)".format(source, sink)
}
