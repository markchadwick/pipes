package pipes.processor

trait Processor[In, Out] {

  type Putable = {
    def put(in: In)
  }

  type Takable = {
    def take(): Out
  }

  def process(inValue: In, put: Out ⇒ Unit): Unit

  def enqueue(inValue: In): Unit

  def run[T](func: ⇒ T): Traversable[Out]
}
