package pipes.processor


class ThreadProcessorSpec extends ProcessorTests {
  def name = "Thread Processor"

  def processor[In, Out](func: (In, Out ⇒ Unit) ⇒ Unit) = {
    new ThreadProcessor[In, Out] {
      def process(in: In, put: Out ⇒ Unit): Unit = func(in, put)
    }
  }
}
