package ex1_fract

object FractApp {

  def main(args: Array[String]): Unit = {
    println(Fract(1, 2) + Fract(1, 2) * Fract(2, 3).neg())
    println(Fract(2, 4))
    println(Fract(1, 2) < Fract(2, 3))
    println(1 + Fract(1, 2))
    println(1 + 1\2)
  }

}