package reduce

object Task6_3_ListReducible {

  def main(args: Array[String]): Unit = {

    import Monoid.*

    val names = List("Susi", "Fritz", "Hans", "Alois", "Josef", "Gust", "Peter")
    val namesReducible = Reducible(names)

    // === Task 6.3 ====================

    // TODO: Task 6.3.a) count the elements
    val n = namesReducible.count
    println(s"Number elements = $n")

    // TODO: Task 6.3.b) concatenate the elements to a single string
    val one = namesReducible.reduce
    println(s"Concatenated = $one")

    // TODO: Task 6.3.c) compute length of all strings
    val length = namesReducible.sum(a => a.length)
    println(s"Length of elements = $length")

    // TODO: Task 6.3.d) create a set of the elements
    val setOfNames = namesReducible.asSet
    println(s"Set of elements = $setOfNames")
  }

}
