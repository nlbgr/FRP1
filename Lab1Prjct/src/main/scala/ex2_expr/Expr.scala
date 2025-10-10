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
    case Neg(s) =>
      simplify(s) match {
        case Lit(v) => Lit(-v)
        case Neg(e) => e
        case e@_ => Neg(e)
      }
    case Recip(s) =>
      simplify(s) match {
        case Lit(v) => Lit(1/v)
        case Recip(e) => e
        case e@_ => Recip(e)
      }
    case Add(l, r) =>
      (simplify(l), simplify(r)) match {
        case (Lit(vl), Lit(vr)) => Lit(vl + vr)
        case (Lit(0), vr) => vr
        case (vl, Lit(0)) => vl
        case (vl, vr) => Add(vl, vr)
      }
    case Mult(l, r) =>
      (simplify(l), simplify(r)) match {
        case (Lit(vl), Lit(vr)) => Lit(vl * vr)
        case (Lit(0), _) => Lit(0)
        case (_, Lit(0)) => Lit(0)
        case (Lit(1), vr) => vr
        case (vl, Lit(1)) => vl
        case (vl, vr) => Mult(vl, vr)
      }
  }
}
  
