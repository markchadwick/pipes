package pipes

import pipes.processor.Processor

object Pipe {

  /*
  def apply[In, Out](func: In ⇒ Traversable[Out]): Pipe[In, Out] = {
    new Pipe[In, Out] {
    }
  }
  */
}

trait Pipe[In, Out] extends Processor[In, Out] {
}
