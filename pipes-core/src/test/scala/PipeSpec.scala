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

  it should "put two pipes together" in {
    val doublePipe = new Pipe[Int, Int] {
      def apply(i: Int) = i * 2 :: Nil
      override def toString = "double"
    }

    val twicePipe = new Pipe[Int, Int] {
      def apply(i: Int) = i :: i :: Nil
      override def toString = "twice"
    }

    val p1 = doublePipe | twicePipe
    val p2 = twicePipe | doublePipe

    p1(5) should equal (10 :: 10 :: Nil)
    p2(5) should equal (10 :: 10 :: Nil)
  }

  it should "optionally append a pipe" in {
    def doublePipe = new Pipe[Int, Int] {
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

  it should "be able to run a pipe twice" in {
    val doublePipe = new Pipe[Int, Int] {
      def apply(i: Int) = i * 2 :: Nil
    }
    doublePipe(3) should equal (6 :: Nil)
    doublePipe(6) should equal (12 :: Nil)
  }

  it should "be able to run three pipes" in {
    // ie: Connect a "normal" pipe to a "piped up" pipe

    def pipe(name: String) = new Pipe[String, String] {
      def apply(s: String) = s :: name :: Nil
      override def toString = name
    }

    val p = pipe("one") | pipe("two") | pipe("three")
    val res = p("s").toList
    p("s").toList should equal (List("s", "three", "two", "three", "one",
                                  "three", "two", "three"))
  }
}
