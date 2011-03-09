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
  def maxQueueSize = 10

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
  def drain: Traversable[Out] = Nil

  def newThread(runnable: Runnable): Thread = {
    val thread = new Thread(runnable)
    thread.setName("[ThreadProcessor]: %s".format(this.toString))
    return thread
  }

  def run[A](func: ⇒ A) = run(defaultPut _)(func _)

  def run[A](put: Out ⇒ Unit)(func: ⇒ A): Traversable[Out] = {
    val thread = newThread(new Runnable {
      def run = {
        println("[%s]...running thread...".format(this))
        Stream.continually(inputQueue.take())
              .takeWhile(_ != None)
              .foreach(v ⇒ process(v.get, put))
        outputQueue.put(None)
        println("[%s]...done running thread...".format(this))
      }
    })
    thread.start()
    func
    inputQueue.put(None)
    Thread.sleep(500)
    val result = Stream.continually(outputQueue.take())
                       .takeWhile(_ != None)
                       .map(_.get) ++ drain ++ {
                          println("[%s]...joining...".format(this))
                          thread.join()
                          println("[%s]...done joining...".format(this))
                        Stream.empty[Out]
                       }

    return result
  }
    


  /*
  override def start() = start(defaultPut _)
  def start(put: Out ⇒ Unit): Unit = {
    thread = Some(newThread(new Runnable {
      def run = {
        Stream.continually(inputQueue.take())
              .takeWhile(_ != None)
              .foreach(v ⇒ process(v.get, put))
        outputQueue.put(None)
      }
    }))
    thread.map(_.start)
  }

  override def stop() = {
    inputQueue.put(None)
    thread.map(_.join)
  }

  def get() = Stream.continually(outputQueue.take())
                    .takeWhile(_ != None)
                    .map(_.get) ++ drain
  */
}
