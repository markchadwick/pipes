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

  def apply(in: In): Traversable[Out] = {
    processor {
      processor.enqueue(in)
    }
    processor.get
  }
}
