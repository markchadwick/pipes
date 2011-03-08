package pipes.processor


class SerialProcessorTests extends ProcessorTests {
  def name = "Serial Processor"
  def processor[In, Out](func: (In, Processor[In, Out]) ⇒ Unit) = {
    new SerialProcessor[In, Out] {
      def process(in: In) = func(in, this)
    }
  }
}
