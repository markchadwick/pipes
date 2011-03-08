package pipes.processor


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
    */
  def process(inValue: In): Unit
  // def withProcess(p: In ⇒ Unit): this.type

  def put(outValue: Out): Unit
  def enqueue(inValue: In): Unit

  def start(): Unit = {}
  def stop(): Unit = {}

  def get(): Traversable[Out]

  def apply[A](func: ⇒ A) {
    start()
    val result = func
    stop()
    result
  }
}
