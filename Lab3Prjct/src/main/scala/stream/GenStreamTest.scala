package stream

import rand.Gen
import rand.Gen.{booleans, ints, intsFromTo, intsTo, valuesOf, words}

object GenStreamTest {

  def main(args: Array[String]): Unit = {

    // Task 9.5.a) Create a stream from generator for integers to 100 and take 10 values and print them out
    intsTo(100).stream.take(10).forEach(println)
    println

    // Task 9.5.b) Create a stream from generator for words of maximal length 20 and take 10 values and print them out
    words(20).stream.take(10).forEach(println)
    println

    // Task 9.5.c) Create a stream from generator for integers from 2 to 100 and find one which is a prime
    //  (use filter and headOption)
    val primeOption: Option[Int] = intsFromTo(2, 100).stream.filter(isPrime).headOption
    println(primeOption)
    println

    // Task 9.5.d) Create a stream from generator for words and find one which contains "x"
    //  (use filter and headOption)
    val xOption: Option[String] = words(10).stream.filter(x => x.toLowerCase.contains("x")).headOption
    println(xOption)
  }
}
