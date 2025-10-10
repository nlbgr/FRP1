package ex1_fract

import java.util.Objects

final class Fract(_n: Int, _d: Int = 1) extends Ordered[Fract] {
  val (numer, denom) = normalize(_n, _d)

  def +(that: Fract): Fract = Fract(numer * that.denom + that.numer * denom, denom * that.denom)

  def -(that: Fract): Fract = Fract(numer * that.denom - that.numer * denom, denom * that.denom)

  def *(that: Fract): Fract = Fract(numer * that.numer, denom * that.denom)

  def /(that: Fract): Fract = this * that.rec()

  def rec(): Fract = Fract(denom, numer)

  def neg(): Fract = Fract(-numer, denom)

  override def toString: String = if (denom == 1) numer.toString else s"$numer \\ $denom"

  override def equals(obj: Any): Boolean = obj match
    case that: Fract => numer == that.numer && denom == that.denom
    case that: Int => numer == that && denom == 1
    case _ => false

  override def compare(that: Fract): Int = {
    val a = numer * that.denom
    val b = that.numer * denom
    a - b
  }
}

object Fract {
  def apply(n: Int, d: Int) = new Fract(n, d)

  def apply(n: Int) = new Fract(n, 1)
}

extension (i: Int) {
  def \(that: Int): Fract = Fract(i, that)
}


given Conversion[Int, Fract] = Fract(_, 1)


def normalize(n: Int, d: Int) = {
  val gcd = calcGcd(n, d)

  if (d < 0) (-n / gcd, d / gcd)
  else (n / gcd, d / gcd)
}

def calcGcd(a: Int, b: Int): Int = {
  if (a < 0 || b < 0) calcGcd(a.abs, b.abs)
  else if (b > a) calcGcd(b, a)
  else if (b == 0) a
  else calcGcd(b, a % b)
}