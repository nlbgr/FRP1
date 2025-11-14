package frp.basics

import scala.util._

object Monads:

  private def traditionalErrorHandling(): Unit = {
    for (s <- Seq("2", "x", "0", "5")) {
      try
        val res = 10 / s.toInt
        println(s"'$s' -> $res")
      catch
        case ex: Throwable =>  println(s"'$s' -> $ex")
    }
  }


  def toInt(s: String): Try[Int] = Try { s.toInt } // in Wahrheit wird apply ausgeführt
  def divide(a: Int, b: Int): Try[Int] = Try { a/b }

  private def monadCallbacks(): Unit = {
    println("--------- toInt ---------")
    for (s <- Seq("2", "x", "0", "5")) {
      val res = toInt(s)
      println(s"'$s' -> $res")
    }

    println("--------- toInt.foreach ---------")
    for (s <- Seq("2", "x", "0", "5")) {
      toInt(s)
        .foreach(res => println(s"'$s' -> $res"))
    }

    println("--------- toInt.failed.foreach ---------")
    for (s <- Seq("2", "x", "0", "5")) {
      toInt(s)
        .failed
        .foreach(res => println(s"'$s' -> $res"))
    }

    println("--------- toInt match ---------")
    for (s <- Seq("2", "x", "0", "5")) {
      toInt(s) match {
        case Success(res) => println(s"'$s' -> $res")
        case Failure(ex) => println(s"'$s' -> $ex")
      }
    }
  }

  private def monadCombinators(): Unit = {
    println("--------- toInt.FlatMap ---------")
    for (s <- Seq("2", "x", "0", "5")) {
      val res = toInt(s).flatMap(divide(10, _))
      println(s"'$s' -> $res")
    }

    println("--------- toInt with for ---------")
    for (s <- Seq("2", "x", "0", "5")) {
      val res =
        for {
          n <- toInt(s)
          q <- divide(10, n)
        } yield q
      println(s"'$s' -> $res")
    }

    println("--------- toInt with nested flatMap ---------")
    for ((s1, s2) <- Seq(("10", "2"), ("10", "x"), ("10", "0"), ("y", "2"))) {
      val res: Try[Int] = toInt(s1).flatMap(n1 =>
        toInt(s2).flatMap(n2 =>
          divide(n1, n2)
        )
      )

      println(s"'$s1, $s2' -> $res")
    }

    println("--------- toInt with nested flatMap ---------")
    for ((s1, s2) <- Seq(("10", "2"), ("10", "x"), ("10", "0"), ("y", "2"))) {
      val res: Try[Int] = for {
        n1 <- toInt(s1)
        n2 <- toInt(s2)
        q <- divide(n1, n2)
      } yield q

      println(s"'$s1, $s2' -> $res")
    }
  }

  def main(args: Array[String]): Unit =
    println("======== traditionalErrorHandling ===========")
    traditionalErrorHandling()
    println()

    println("============= monadCallbacks ================")
    monadCallbacks()
    println()

    println("============ monadCombinators ===============")
    monadCombinators()
    println()
  end main

end Monads