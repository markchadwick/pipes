package pipes.processor


class ThreadProcessorTests extends ProcessorTests {
  def name = "Thread Processor"
  def processor[In, Out](func: (In, Processor[In, Out]) ⇒ Unit) = {
    new ThreadProcessor[In, Out] {
      def process(in: In) = func(in, this)
    }
  }
}
