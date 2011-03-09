package pipes.scheduler

import pipes.processor.Processor


trait Puttable[T] {
  def put(v: Option[T]): Unit
  def put(v: T): Unit = put(Some(v))
}

trait Scheduler {
  def run[In, Out](processor: Processor[In, Out])(func: Puttable[In] ⇒ Any): Traversable[Out]
}
