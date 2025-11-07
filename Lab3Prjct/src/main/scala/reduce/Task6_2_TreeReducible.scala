package reduce

import tree._

object Task6_2_TreeReducible {

  def main(args: Array[String]): Unit = {

    import Monoid.*
    import tree.BinTree.*

    val nameTree : BinTree[String] =
      node("Susi",
        node(
          "Fritz",
          node(
            "Alois",
            node(
              "Gust", empty, empty
            ),
            node("Peter", empty, empty)
          ),
          node("Josef", empty, empty)
        ),
        node("Hans", empty, empty)
      )

    val namesReducible = Reducible(nameTree)

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
