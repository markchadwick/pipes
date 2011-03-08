package pipes.processor

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.BlockingQueue


/** A `Processor` which computes events entirely serially in a self-contained
  * thread. Boundies have been set for the thread below, but they should be
  * overriden in specific cases */
trait ThreadProcessor[In, Out] extends Processor[In, Out] {

  protected val inputQueue = newQueue[Option[In]](maxInputQueueSize)
  protected val outputQueue = newQueue[Option[Out]](maxOutputQueueSize)

  /** Default maximum queue size. If neither `maxInputQueueSize` or
    * `maxOutputQueueSize` are overriden, this will serve as the default value
    * for both. */
  def maxQueueSize = 100

  /** Maximum size of the input queue. Whoever is feeding values to this
    * `Processor` will block when this number of times is pending execution. */
  def maxInputQueueSize = maxQueueSize

  /** Maximum number of items that can be put on this output queue without being
    * consumed. This thread will block until a reasonable number of results have
    * been consumed. Be warely of deadlocks. */
  def maxOutputQueueSize = maxQueueSize

  /** Create a new (input or output) queue for this `ThreadProcessor`. It is up
    * to the implementation to set the necessary bounds for the queue (if
    * necessary).
    *
    * @see maxInputQueueSize
    * @see maxOutputQueueSize
    * @tparam A Element type of the quque
    * @return A new blocking queue containing type `A`.
    */ 
  protected def newQueue[A](size: Int): BlockingQueue[A] =
    new LinkedBlockingQueue[A](size)


  def enqueue(in: In) = inputQueue.put(Some(in))
  def defaultPut(out: Out) = outputQueue.put(Some(out))

  def newThread(runnable: Runnable): Thread = {
    val thread = new Thread(runnable)
    thread.setName("[ThreadProcessor]: %s".format(this.toString))
    return thread
  }

  override def start() = start(defaultPut _)
  def start(put: Out ⇒ Unit): Unit = {
    val thread = newThread(new Runnable {
      def run = {
        Stream.continually(inputQueue.take())
              .takeWhile(_ != None)
              .foreach(v ⇒ process(v.get, put))
        outputQueue.put(None)
      }
    })
    thread.start()
  }

  override def stop() = {
    inputQueue.put(None)
  }

  def get() = Stream.continually(outputQueue.take())
                    .takeWhile(_ != None)
                    .map(_.get)
}
