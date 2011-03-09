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

  it should "run without the explicit in type annotation" is (pending)
}
