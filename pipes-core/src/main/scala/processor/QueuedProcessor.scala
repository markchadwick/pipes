package pipes.processor

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.BlockingQueue


protected trait QueueFeatures {
  protected def maxQueueSize = 10

  protected def newQueue[A](size: Int): BlockingQueue[A] =
    new LinkedBlockingQueue[A](size)
}


trait QueuedInputProcessor[In, Out] extends Processor[In, Out]
                                    with QueueFeatures {
  protected def maxInputQueueSize = maxQueueSize
  protected val inputQueue = newQueue[Option[In]](maxInputQueueSize)
}


trait QueuedOutputProcessor[In, Out] extends Processor[In, Out]
                                     with QueueFeatures {
  protected def maxOutputQueueSize = maxQueueSize
  protected val outputQueue = newQueue[Option[Out]](maxOutputQueueSize)
}


trait QueuedProcessor[In, Out] extends QueuedInputProcessor[In, Out]
                               with QueuedOutputProcessor[In, Out]
