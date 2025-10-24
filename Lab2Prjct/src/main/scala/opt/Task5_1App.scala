package opt

import java.util.Scanner

// Task 5.1: Alternative values for None

object Task5_1App extends App {

  val scn = new Scanner(System.in)
  val bds: Map[String, Int] = Map("x" -> 1, "y" -> 4, "z" -> 0)

  // a) Test the value and then access it with get.

  val optX = bds.get("x")
  if (optX.isDefined) {
    println(s"x is ${optX.get}")
  } else {
    println(s"x is undefined")
  }

  val optU = bds.get("u")
  if (optU.isDefined) {
    println(s"u is ${optU.get}")
  } else {
    println(s"u is undefined")
  }

  // b)	Use the method getOrElse to specify an alternative value in case of missing value
  println(s"x is ${optX.getOrElse(-1)}")
  println(s"u is ${optU.getOrElse(-1)}")

  // c)	Use the method elseGet to read in an alternative value from the user (with Scanner scn) if a value is missing

  val uOptOrElse: Option[Int] = optU.orElse{
    println("Enter a value")
    try {
      Some(scn.nextInt())
    } catch
      case e: Exception => None
  }
  println(uOptOrElse.getOrElse(-1))

  val xOptOrElse : Option[Int] = optX.orElse{
    println("Enter a value")
    try {
      Some(scn.nextInt())
    } catch
      case e: Exception => None
  }
  println(xOptOrElse.getOrElse(-1))
}

