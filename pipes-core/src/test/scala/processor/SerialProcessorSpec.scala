package pipes.processor


class SerialProcessorSpec extends ProcessorTests {
  def name = "Serial Processor"

  def processor[In, Out](func: (In, Out ⇒ Unit) ⇒ Unit) = {
    new SerialProcessor[In, Out] {
      def process(in: In, put: Out ⇒ Unit): Unit = func(in, put)
    }
  }
}
