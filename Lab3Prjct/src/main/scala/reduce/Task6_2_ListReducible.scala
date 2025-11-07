package reduce

import reduce.Monoid.{intPlusMonoid, stringMonoid}

object Task6_2_ListReducible {

  def main(args: Array[String]): Unit = {

    import Monoid.*

    val names = List("Susi", "Fritz", "Hans", "Alois", "Josef", "Gust", "Peter")
    val namesReducible: Reducible[String] = Reducible(names)

    // TODO: Task 6.2.a) count the elements
    val n = namesReducible.reduceMap(a => 1) // (using intPlusMonoid)
    println(s"Number elements = $n")

    // TODO: Task 6.2.b) concatenate the elements to a single string
    val one = namesReducible.reduce // (using stringMonoid)
    println(s"Concatenated = $one")

    // TODO: Task 6.2.c) compute length of all strings
    val length = namesReducible.reduceMap(a => a.length)
    println(s"Length of elements = $length")

    // TODO: Task 6.2.d) create a set of the elements
    val setOfNames = namesReducible.reduceMap(a => Set(a))
    println(s"Set of elements = $setOfNames")
  }

}
