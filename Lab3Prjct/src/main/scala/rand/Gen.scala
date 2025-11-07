package rand

import rand.Gen.{intsFromTo, unit}
import stream.*

trait Gen[A] extends (Long => (A, Long)) {

  def flatMap[B](f: A => Gen[B]) : Gen[B] =
    seed => {
      val (a, nextSeed) = this(seed)
      f(a)(nextSeed)
    }

  def map[B](f: A => B) : Gen[B] =
    this.flatMap(a => Gen.unit(f.apply(a)))

  def lists(len: Int): Gen[List[A]] = {
    if (len == 0) {
      Gen.unit(List.empty)
    } else {

      this.flatMap(e =>
        lists(len - 1).map(l => e :: l)
      )

      // alternativ
      //    for {
      //      e <- this
      //      l <- lists(len - 1)
      //    } yield e :: l
    }
  }

  def listsOfLengths(minLen: Int, maxLen: Int): Gen[List[A]] = intsFromTo(minLen, maxLen).flatMap(x => lists(x))

  // TODO: Tast 9.5
  def stream(seed: Long) : Stream[A] = {
    val (a, s) = this(seed)
    Stream(a, stream(s))
  }

  def stream : Stream[A] = stream(System.currentTimeMillis())
}

object Gen {

  def unit[A](a: A) : Gen[A] = (seed => (a, seed))

  val ints: Gen[Int] = seed => {
    val randInt = (seed >>> 16).toInt
    val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
    (randInt, newSeed)
  }

  val posInts: Gen[Int] = ints.map(x => x.abs)
  val doubles: Gen[Double] = posInts.map(x => x.toDouble / Int.MaxValue)

  def doublesFromTo(from: Double, to: Double): Gen[Double] = doubles.map(x => x * (to - from) + from)
  def doublesTo(to: Double) : Gen[Double] = doublesFromTo(0, to)

  def intsFromTo(from: Int, to: Int) : Gen[Int] = doublesFromTo(from, to).map(x => x.toInt)
  def intsTo(to: Int) : Gen[Int] = intsFromTo(0, to)


  def booleans(prob: Double) = doubles.map(x => x < prob)

  def valuesOf[A](values: A*) : Gen[A] = intsTo(values.size).map(values(_))

  val letters : Gen[Char] = valuesOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
  //'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')

  def words(len: Int) : Gen[String] = letters.listsOfLengths(2, len).map(l => l.mkString)
}
