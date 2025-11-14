package frp.basics

object ScalaBasics:

  private def implicitConversion(): Unit = {
    val numbers = intWrapper(1).to(3)
    // val numbers = RichInt(1).to(3)
    // val numbers = 1 to 3
    println(s"numbers -> $numbers")

    case class MyInt(value: Int):
      def isBig: Boolean = value >= 1000

    given Conversion[Int, MyInt] = i => MyInt(i) // Converstion wird durchgeführt, wenn benötigt wegen given

    println(s"1100.isBig = ${1100.isBig}")
  }


  private def extensionMethods(): Unit = {
    extension (i: Int)
      def isEven: Boolean = i % 2 == 0
      def isOdd: Boolean = !isEven

    println(s"2.isEven -> ${2.isEven}")
    println(s"2.isOdd -> ${2.isOdd}")
  }


  private def currying(): Unit = {
    val numbers = 1 to 3
    val sumSquares = numbers.foldLeft(0)((sum, n) => sum + n*n)

    println(s"sumSquares = $sumSquares")

    val foldLeftStr = numbers.foldLeft("0")((acc, i) => s"f($acc,$i)")
    println(s"foldLeftStr = $foldLeftStr")

    val foldRightStr = numbers.foldRight("0")((acc, i) => s"f($acc,$i)")
    println(s"foldRightStr = $foldRightStr")

    val f1: ((String, Int) => String) => String = numbers.foldLeft("0")
    val result = f1((acc, n) => s"f($acc, $n)")
    println(s"result = $result")
  }


  private object Orderings:
    trait IntOrdering:
      def less(a: Int, b: Int): Boolean

    object AscendingIntOrdering extends IntOrdering:
      override def less(a: Int, b: Int): Boolean = a < b

    object DescendingIntOrdering extends IntOrdering:
      override def less(a: Int, b: Int): Boolean = a >= b
  end Orderings

  private def implicitParameters(): Unit = {
    import Orderings._

    implicit val defaultOrdering: IntOrdering = AscendingIntOrdering

    def min(a: Int, b: Int)(implicit ord: IntOrdering): Int = if ord.less(a,b) then a else b

    println(s"min(3, -4)(AscendingIntOrdering) -> ${min(3, -4)(AscendingIntOrdering)}")
    println(s"min(3, -4) -> ${min(3, -4)}")
  }

  private def givens(): Unit = {
    import Orderings._

    //given IntOrdering = AscendingIntOrdering
    given IntOrdering with
      override def less(a: Int, b: Int): Boolean = math.abs(a) < math.abs(b)

    def min(a: Int, b: Int)(implicit ord: IntOrdering): Int = if ord.less(a, b) then a else b

    println(s"min(3, -4)(AscendingIntOrdering) -> ${min(3, -4)(using AscendingIntOrdering)}")
    println(s"min(3, -4) -> ${min(3, -4)}")
  }

  private def callByName(): Unit = {}

  private def companionObject(): Unit = {}

  private def functionTypes(): Unit = {
    val twice: Int => Int = (n: Int) => 2*n
    val twice2: Function[Int, Int] = (n: Int) => 2*n

    val sign: PartialFunction[Int, -1 | 0 | 1] = {
      case n if n > 0 => 1
      case n if n < 0 => -1
      case _ => 0
    }

    println(s"sign(-10) -> ${sign(-10)}")
    println(s"sign(10) -> ${sign(10)}")
    println(s"sign(0) -> ${sign(0)}")
    println(s"sign.isDefinedAt(-10) -> ${sign.isDefinedAt(-10)}")
  }

  @main
  def main(): Unit =
    println("========= implicitConversion ======")
    implicitConversion()

    println("========= extensionMethods ======")
    extensionMethods()

    println("========= currying ======")
    currying()

    println("========= implicitParameters ======")
    implicitParameters()

    println("========= givens ======")
    givens()

    println("========= callByName ======")
    callByName()

    println("========= companionObject ======")
    companionObject()

    println("========= functionTypes ======")
    functionTypes()
  end main

end ScalaBasics

