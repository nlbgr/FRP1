package rand

import Gen.*

object GenTests {

  def main(args: Array[String]): Unit = {

    val b10: Gen[List[Int]] = intsTo(100).lists(10)
    val (bs, _) = b10(3293)
    for (b <- bs) println(b)
    println

    val i10: Gen[List[Boolean]] = booleans(0.5).lists(10)
    val (is, _) = i10(40591)
    for (i <- is) println(i)
    println

    val nIntListLists:  Gen[List[List[Int]]] = intsTo(100).listsOfLengths(2, 10).lists(10)
    val (r1, _) = nIntListLists(34243)
    for (l <- r1)
      println(l)
    println

    val nWordsLists: Gen[List[String]] = words(10).lists(10)
    val (r3, _) = nWordsLists(23987)
    for (l <- r3) println(l)
    println

    val nElemsLists: Gen[List[String]] = valuesOf("A", "B", "C").lists(10)
    val (r4, _) = nElemsLists(87236481)
    for (l <- r4) println(l)

    // 10 lists with strings that consist of "A", "B", "C"
    val nWordListsABC: Gen[List[String]] =
      valuesOf("A", "B", "C").listsOfLengths(10, 10).flatMap(x => Gen.unit(x.mkString)).lists(10)
    val (r5, _) = nWordListsABC(87236481)
    for (l <- r5) println(l)

    // 10 random binaries as string with 5 to 10 bits
    val randomBinary: Gen[List[String]] =
      booleans(0.5).flatMap(x => Gen.unit(if x then "1" else "0")).listsOfLengths(5, 10).flatMap(x => Gen.unit(x.mkString)).lists(10)
    val (r6, _) = randomBinary(87236481)
    for (l <- r6) println(l)
  }
}
