package pipes

import java.util.concurrent.Executors


object Pipes {
  def poolSize = Runtime.getRuntime.availableProcessors * 2
  val pool = Executors.newFixedThreadPool(poolSize)

  implicit def funcToPipe[A, B](func: A ⇒ Traversable[B]): Pipe[A, B] = 
    new Pipe[A, B] { def apply(in: A) = func(in) }

/*
  implicit def traversableToPipe[A](t: Traversable[A]): Source[A] =
    new Source[A] { def apply(): Traversable[A] = t }
*/

}
