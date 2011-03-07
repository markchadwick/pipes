package pipes.concurrent

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import pipes.Pipe


class ConcurrentPipeSpec extends FlatSpec with ShouldMatchers {
  behavior of "Concurrent Pipe"

  it should "convert a simple entry" in {
    val doubler = new Pipe[Int, Int] {
      def apply(i: Int) = i * 2 :: Nil
    }
    val pipe = ConcurrentPipe(doubler)
    pipe(12).toList should equal (List(24))
  }

  it should "run tasks concurrently" in {
    def busyPipe(loops: Int) = ConcurrentPipe(new Pipe[Int, Int] {
      def apply(l: Int) = {
        (1 to loops).map(i ⇒ (1 to l).foldLeft(0)(_ + _))
      }
      override def toString = "busy%s".format(loops)
    })

    val pipe = busyPipe(20) | busyPipe(10) | busyPipe(5)
    println(pipe)
  }
}
