package pipes

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class ConversionsSpec extends FlatSpec with ShouldMatchers {
  behavior of "Conversions"

  it should "convert a function to a pipe" in {
    import pipes.Pipes._

    val doubleExplicit = new Pipe[Int, Int] {
      def apply(i: Int) = i * 2 :: Nil
    }

    val quad1 = doubleExplicit | ((x: Int) ⇒ x * 2 :: Nil)
    val quad2 = ((x: Int) ⇒ x * 2 :: Nil) | doubleExplicit

    quad1(2) should equal (8 :: Nil)
    quad2(2) should equal (8 :: Nil)
  }

  it should "convert a list to a source" in {
    import pipes.Pipes._

    val double = new Pipe[Int, Int] {
      def apply(i: Int) = i * 2 :: Nil
    }

    val pipe = List(1, 2, 3) | double

    pipe() should equal (List(2, 4, 6))
    pipe(null) should equal (List(2, 4, 6))
  }

  it should "chain to implicit conversions" in {
    import pipes.Pipes._

    val pipe = List(1, 2, 3) | ((x: Int) ⇒ x * 2 :: Nil)
    pipe() should equal (List(2, 4, 6))
  }
}
