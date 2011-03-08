package pipes.processor

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers


trait ProcessorTests extends FlatSpec with ShouldMatchers {
  def name: String
  def processor[In, Out](func: (In, Processor[In, Out]) ⇒ Unit): Processor[In, Out]

  behavior of name

  it should "transform a value" in {
    val proc = processor[Int, Int] { case (in, proc) ⇒
      proc.put(in * 2)
    }

    proc { p ⇒
      p.enqueue(1)
      p.enqueue(2)
      p.enqueue(3)
    }

    proc.get.toList should equal (List(2, 4, 6))
  }

  it should "handle multiple output values" in {
    val proc = processor[String, String] { case (in, proc) ⇒
      proc.put(in)
      proc.put(in)
    }

    proc.start()
    proc.enqueue("hi")
    proc.enqueue("there")
    proc.stop()

    val results = proc.get().toList
    results should have size (4)
    results(0) should equal ("hi")
    results(1) should equal ("hi")
    results(2) should equal ("there")
    results(3) should equal ("there")
  }
}
