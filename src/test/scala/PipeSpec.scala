package pipes

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import pipes.processor.Processor

class PipeSpec extends FlatSpec with ShouldMatchers {
  behavior of "Pipe"

  it should "define a pipe" in {
    val doublePipe = new Pipe[Int, Int] {
      def apply(i: Int) = i * 2 :: Nil
    }
    doublePipe(3) should equal (6 :: Nil)
  }

  it should "put two pipes to gether" in {
    val doublePipe = new Pipe[Int, Int] {
      def apply(i: Int) = i * 2 :: Nil
    }

    val twicePipe = new Pipe[Int, Int] {
      def apply(i: Int) = i :: i :: Nil
    }

    val p1 = doublePipe | twicePipe
    val p2 = twicePipe | doublePipe

    p1(5) should equal (10 :: 10 :: Nil)
    p2(5) should equal (10 :: 10 :: Nil)
  }

  it should "optionally append a pipe" in {
    val doublePipe = new Pipe[Int, Int] {
      def apply(i: Int) = i * 2 :: Nil
    }

    val twoDouble = doublePipe | Some(doublePipe)
    val oneDouble = doublePipe | None

    twoDouble(3) should equal (12 :: Nil)
    oneDouble(3) should equal (6 :: Nil)
  }

  it should "be an infix operation" in {
    def pipe(name: String) = new Pipe[String, String] {
      def apply(s: String) = s :: Nil
      override def toString = name
    }

    val p = pipe("one") | pipe("two") | pipe("three")
    p.toString should equal ("((one ⇒ two) ⇒ three)")
  }
}
