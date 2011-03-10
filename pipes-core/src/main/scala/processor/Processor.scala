package pipes.processor


trait Processor[In, Out] {
  def process(input: In, put: Out ⇒ Unit)
}
