package pipes

import pipes.processor.Processor
import pipes.processor.ProcessorFactory
import pipes.processor.SerialProcessorFactory

object Pipe {
  def apply[In, Out](func: In ⇒ Traversable[Out])
                    (implicit fact: ProcessorFactory=SerialProcessorFactory): Pipe[In, Out] = {

    return new Pipe[In, Out] {
      val processor = fact(func)
    }
  }

}

trait Pipe[In, Out] {
  val processor: Processor[In, Out]

  def |[A](pipe: Pipe[Out, A]): Pipe[In, A] = {
    null
  }

  /** Execute this pipe for the given input
    *
    * @param in Input value to process
    * @return `Traversable` of the results of this Processor
    */
  def apply(in: In): Traversable[Out] = {
    processor.apply(p ⇒ p.enqueue(in))
    processor.get()
  }
}
