package pipes.processor

import scala.collection.mutable.Queue

/** A `Processor` which computes events entirely serially. */
trait SerialProcessor[In, Out] extends Processor[In, Out] {
  val outQueue = Queue.apply[Out]()

  def enqueue(in: In) = process(in)
  def put(out: Out) = outQueue += out

  def get() = {
    val results = outQueue.clone()
    outQueue.clear()
    results
  } 
}

object SerialProcessorFactory extends ProcessorFactory {
  def apply[In, Out](func: In ⇒ Traversable[Out]): Processor[In, Out] = {
    new SerialProcessor[In, Out] {
      def process(in: In) = func(in).foreach(put)
    }
  }
}
