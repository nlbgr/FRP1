package reduce

object Task6_2_ListReducible {

  def main(args: Array[String]): Unit = {

    import Monoid.*

    val names = List("Susi", "Fritz", "Hans", "Alois", "Josef", "Gust", "Peter")
    val namesReducible = Reducible(names)

    // === Task 6.2 ====================

    //a) count the elements
    val n = namesReducible.reduceMap(name => 1)
    println(s"Number elements = $n")

    //b) concatenate the elements to a single string
    val one = namesReducible.reduce
    println(s"Concatenated = $one")

    //c) compute length of all strings
    val length = namesReducible.reduceMap(_.length)
    println(s"Length of elements = $length")

    //d) create a set of the elements
    val setOfNames = namesReducible.reduceMap(Set(_))(using setMonoid)
    println(s"Set of elements = $setOfNames")
  }

}
