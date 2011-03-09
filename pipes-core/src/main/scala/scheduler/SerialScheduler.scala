package pipes.scheduler

import scala.collection.mutable.Queue

import pipes.processor.Processor

class SerialScheduler extends Scheduler {

  def run[In, Out](processor: Processor[In, Out])
                  (func: Puttable[In] ⇒ Any): Traversable[Out] = {
    var output = Queue[Out]()

    func(new Puttable[In] {
      def put(v: Option[In]) = v foreach { value ⇒
        processor.process(value, output += _)
      }
    })
    output
  }

}
