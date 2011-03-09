package pipes.processor

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers


trait ProcessorTests extends FlatSpec with ShouldMatchers {
  def name: String
  def processor[In, Out](func: (In, Out ⇒ Unit) ⇒ Unit): Processor[In, Out]

  behavior of name

  it should "transform a value" in {
    val proc = processor[Int, Int] { case (in, put) ⇒
      println("[proc] got %s".format(in))
      put(in * 2)
    }

    val result = proc.run {
      proc.enqueue(1)
      proc.enqueue(2)
      proc.enqueue(3)
    }

    result.toList should equal (List(2, 4, 6))
  }

  /*
  it should "handle multiple output values" in {
    val proc = processor[String, String] { case (in, put) ⇒
      put(in)
      put(in)
    }

    var results = proc.run {
      proc.enqueue("hi")
      proc.enqueue("there")
    }.toList

    results should have size (4)
    results(0) should equal ("hi")
    results(1) should equal ("hi")
    results(2) should equal ("there")
    results(3) should equal ("there")
  }
  */
}
