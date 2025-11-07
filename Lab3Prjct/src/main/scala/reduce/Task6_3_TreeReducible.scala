package reduce

import tree.*

object Task6_3_TreeReducible {

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
