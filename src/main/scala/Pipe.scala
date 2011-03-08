package pipes

import pipes.processor.Processor
import pipes.processor.ThreadProcessor


object Pipe {
}

trait Pipe[In, Out] extends ThreadProcessor[In, Out] {
  def apply(in: In): Traversable[Out]

  def process(in: In, put: Out ⇒ Unit) =
    apply(in).foreach(put)

  def |[A](other: Pipe[Out, A]): Pipe[In, A] = {
    val me = this
    new Pipe[In, A] {
      override def process(in: In, put: A ⇒ Unit) {
        me.process(in, other.enqueue _)
      }
      def apply(in: In): Traversable[A] = {
        me.start()
        other.start()
        me.enqueue(in)
        me.stop()
        other.stop()
        other.get()
      }
    }
  }
}

// trait Pipe[In, Out] {
//   val processor: Processor[In, Out]
// 
//   def |[A](pipe: Pipe[Out, A]): Pipe[In, A] = {
//     null
//   }
// 
//   /** Execute this pipe for the given input
//     *
//     * @param in Input value to process
//     * @return `Traversable` of the results of this Processor
//     */
//   def apply(in: In): Traversable[Out] = {
//     processor.apply(p ⇒ p.enqueue(in))
//     processor.get()
//   }
// }
