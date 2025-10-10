package ex2_expr

sealed trait Expr
case class Lit(value: Double) extends Expr {
  override def toString: String = value.toString
}
case class Var(name: String) extends Expr {
  override def toString: String = name
}
sealed trait BinExpr(val left: Expr, val right: Expr) extends Expr
case class Add(l: Expr, r: Expr) extends BinExpr(l, r) {
  override def toString: String = s"( $l + $r )"
}
case class Mult(l: Expr, r: Expr) extends BinExpr(l, r) {
  override def toString: String = s"( $l * $r )"
}
sealed trait UnExpr(val sub: Expr) extends Expr
case class Neg(s: Expr) extends UnExpr(s) {
  override def toString: String = s"( -$s )"
}
case class Recip(s: Expr) extends UnExpr(s) {
  override def toString: String = s"( 1 / $s )"
}

def infix(expr: Expr): String = {
  def op(binExpr: BinExpr): String = {
    expr match {
      case Add(_, _) => "+"
      case Mult(_, _) => "*"
    }
  }

  expr match {
    case Lit(v) => v.toString
    case Var(n) => n
    case b: BinExpr => s"( ${b.left} ${op(b)} ${b.right} )"
    case Neg(s) => s"( -$s )"
    case Recip(s) => s"( 1 / $s )"
  }
}

def eval(expr: Expr, bds: Map[String, Double]): Double = {
  def eval(expr: Expr): Double = {
    expr match {
      case Lit(v) => v
      case Var(n) => bds(n)
      case Add(l, r) => eval(l) + eval(r)
      case Mult(l, r) => eval(l) * eval(r)
      case Neg(s) => -eval(s)
      case Recip(s) => 1 / eval(s)
    }
  }

  eval(expr)
}

def simplify(expr: Expr): Expr = {
  expr match {
    case e@Lit(_) => e // expr wäre nicht typ Lit sondern Expr
    case e@Var(_) => e

  }
}
  
