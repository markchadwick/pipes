package pipes.processor

import pipes.scheduler.Scheduler
import pipes.scheduler.Puttable

trait Processor[In, Out] {
  def process(input: In, put: Out ⇒ Unit)

  /*
  def run(implicit scheduler: Scheduler): Traversable[Out] =
    scheduler.run(this)(scheduler.input)
  */
}
