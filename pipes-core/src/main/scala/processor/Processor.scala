package pipes.processor

/*
object Processor {
  def apply[A, B](func: A ⇒ Traversable[B]) = new ThreadProcessor[A, B] {
    def process(in: A, put: B ⇒ Unit) = func(in).foreach(put)
  }
}
*/

/** A processor is a unit of work which is able to take some value and put some
  * other values on a queue. The exact deatils of how it is executed (or
  * optionally parallelized is entirely up to the implemention)
  *
  * ==Creating a Processor==
  *
  * A basic `Processor` is as follows:
  * {{{
  * class DoublerProcessor extends Processor[String, String] {
  *   def process(in: String) {
  *     put(in)
  *     put(in)
  *   }
  * }
  * }}}
  * '''Note:''' The above example needs a concrete `Processor` to run correctly.
  *
  * ==Consuming A Processor==
  * When using a `Processor`, it is entirely your responsibility to call
  * `start` before interacting with the processor, and calling `stop` when
  * finished.  There are no guarentees a processor will behave correctly
  * otherwise.
  */
trait Processor[In, Out] {

  /** Consume a single unit of work, optionally calling `put` for each result
    * computed.
    *
    * @param inValue Value to process
    * @param put Function to call for each result value
    */
  def process(inValue: In, put: Out ⇒ Unit): Unit

  def enqueue(inValue: In): Unit

  def run[A](func: ⇒ A): Traversable[Out]
}
