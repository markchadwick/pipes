package pipes

trait Pipe[In, Out] {
  def apply(in: In): Traversable[Out]

  def |[A](other: Pipe[Out, A]): Pipe[In, A] = {
    val me = this
    new Pipe[In, A] {
      def apply(in: In) = me.apply(in).flatMap(other.apply)
      override def toString = "(%s → %s)".format(me, other)
    }
  }

  def |(other: Option[Pipe[Out, Out]]): Pipe[In, Out] = {
    other match {
      case None ⇒ this
      case Some(pipe) ⇒ this | pipe
    }
  }
}

trait Source[Out] extends Pipe[Null, Out] {
  def apply(): Traversable[Out]
  def apply(n: Null): Traversable[Out] = apply()

  override def |[A](other: Pipe[Out, A]): Source[A] = {
    val me = this
    new Source[A] {
      def apply() = me.apply().flatMap(other.apply)
    }
  }
}
