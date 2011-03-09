package pipes

import pipes.processor.ThreadProcessor


trait Pipe[In, Out] extends ThreadProcessor[In, Out] {
  def apply(in: In): Traversable[Out]

  def process(in: In, put: Out ⇒ Unit) = apply(in).foreach(put)

  def |[A](other: Pipe[Out, A]): Pipe[In, A] = {
    val me = this
    new Pipe[In, A] {
      def apply(in: In): Traversable[A] = {
        other.run {
          me.run(other.enqueue _) {
            me.enqueue(in)
          }
        }
      }

      override def toString = "(%s ⇒ %s)".format(me, other)
    }
  }

  def |(other: Option[Pipe[Out, Out]]): Pipe[In, Out] =
    other match {
      case None ⇒ this
      case Some(pipe) ⇒ this | pipe
    }
}
