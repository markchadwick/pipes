package pipes.processor

import scala.collection.mutable.Queue

/** A `Processor` which computes events entirely serially. */
trait SerialProcessor[In, Out] extends Processor[In, Out] {
  val outQueue = Queue.apply[Out]()

  def enqueue(in: In) = process(in, defaultPut _)
  protected def defaultPut(out: Out) = outQueue += out

  def get() = {
    val results = outQueue.clone()
    outQueue.clear()
    results
  } 
}
