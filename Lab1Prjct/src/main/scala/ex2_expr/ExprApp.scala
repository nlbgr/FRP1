package ex2_expr

import ex2_expr._

object ExprApp {

  def main(args: Array[String]): Unit = {
    //println ("Hello World")
    //val e1 = Add(Lit(1), Min(Var("x")))
    val e1 = Add(Lit(1), Neg(Var("x")))
    println(e1)
    println(infix(e1))
    print(eval(e1, Map("x" -> 10)))
  }

}
