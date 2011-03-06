Pipes
=====
Little library that I always ended up reimplementing for various projects. The
idea is the define a unit of work as a pipe, which can be composed together to
form small programs.

An example!
-----------

    import pipes.Pipe

    class Doubler extends Pipe[Int, Int] {
      def apply(i: Int) = i * 2 :: Nil
    }

    class ToStringer extends Pipe[AnyRef, String] {
      def apply(a: AnyRef) = a.toString :: Nil
    }

    class Repeater(times: Int) extends Pipe[String, String] {
      def apply(s: String) = s * i
    }

    object Main {
      def main(args: Array[String]): Unit {
        val pipe = new Doubler() | new ToStringer() | new Repeater(2)
        println(pipe(3))
      }
    }

This should print out:

    List("6", "6")

Some Implicits
--------------
Implicits live an `pipes.Pipes._` so importing them will give you some goodies

    scala> import pipes.Pipes._
    import pipes.Pipes._

    scala> val p = List(1, 2, 3) | ((x: Int) => x + 1 :: Nil)
    p: pipes.Source[Int] = pipes.Source$$ana$1@12345

    scala> p()
    res1: Traversable[Int] = List(2, 3, 4)

    scala> val evens = p | ((x: Int) => if(x % 2 == 0) x :: Nil else Nil)
    p: pipes.Source[Int] = pipes.Source$$ana$1@67890

    scala> evens()
    res2: Traversable[Int] = List(2, 4)

