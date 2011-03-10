package pipes.scheduler

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import pipes.processor.Processor


trait SchedulerTests extends FlatSpec with ShouldMatchers {
  def name: String
  def scheduler: Scheduler

  it should "schedule a simple processor" in {
    val double = new Processor[Int, Int] {
      def process(in: Int, put: Int ⇒ Unit) {
        put(in * 2)
      }
    }

    val result = scheduler.run(double) { in: Puttable[Int] ⇒
      in.put(3)
      in.put(9)
    }

    result.toList should equal (List(6, 18))
  }

  it should "handle multiple output values" in {
    val twice = new Processor[String, String] {
      def process(in: String, put: String ⇒ Unit) {
        put(in)
        put(in)
      }
    }

    val results = scheduler.run(twice) { in: Puttable[String] ⇒
      in.put("hi")
      in.put("there")
    }.toList

    results should have size (4)
    results(0) should equal ("hi")
    results(1) should equal ("hi")
    results(2) should equal ("there")
    results(3) should equal ("there")
  }

  it should "run without the explicit in type annotation" is (pending)
}
