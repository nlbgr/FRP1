package opt

import expr.{Add, Expr, Lit, Min, Mult, Rec, Var}

import java.util.Scanner
import scala.util.{Try, Success, Failure}

// Task 5.5: Expression evaluation with Option

def evalTry(expr: Expr, bds: Map[String, Double]): Try[Double] =
  expr match {
    case Lit(v) => Success(v)
    case Var(n) => Try(bds.apply(n))
    case Add(l, r) => evalTry(l, bds).flatMap(lr =>
      evalTry(r, bds).map(rr => lr + rr)
    )
    case Mult(l, r) => evalTry(l, bds).flatMap(lr =>
      evalTry(r, bds).map(rr => lr * rr)
    )
    case Min(s) => evalTry(s, bds).map(s => -s)
    case Rec(s) => evalTry(s, bds).flatMap(s =>
      if (s == 0) Failure(Exception("Division by 0")) else Success(1/s)
    )
  }

object Task5_5ExprEvalTry extends App {

  val bds = Map("x" -> 2.0, "y" -> 3.0, "z" -> 0.0)

  val e1 = Add(Lit(1), Min(Var("x")))
  val tryR1 = evalTry(e1, bds)
  println (s"${e1.toString} = ${tryR1.map(_.toString).getOrElse("undefined")}")

  val e2 = Mult(Lit(1), Min(Var("u")))
  val tryR2 = evalTry(e2, bds)
  //println(tryR2.get)
  println (s"${e2.toString} = ${tryR2.map(_.toString).getOrElse("undefined")}")

  val e3 = Mult(Lit(1), Rec(Var("z")))
  val tryR3 = evalTry(e3, bds)
  println (s"${e3.toString} = ${tryR3.map(_.toString).getOrElse("undefined")}")

}
