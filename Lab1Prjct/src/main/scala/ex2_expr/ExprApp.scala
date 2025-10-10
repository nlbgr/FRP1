package ex2_expr

import ex2_expr._

object ExprApp {

  def main(args: Array[String]): Unit = {
    val e1 = Add(Lit(1), Neg(Var("x")))
    println(e1)
    println(infix(e1))
    println(eval(e1, Map("x" -> 10)))

    val e2 = Mult(Add(Lit(1), Neg(Lit(1))), Lit(100))
    println(simplify(e2))
  }
}
