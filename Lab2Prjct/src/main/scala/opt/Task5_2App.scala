package opt

// Task 5.2: Pattern matching on Option values

object Task5_2App extends App {

  val bds: Map[String, Int] = Map("x" -> 1, "y" -> 4, "z" -> 0)

  val optX = bds.get("x")

  // Pattern matching optX
  println(optX match
    case None => "no value for x"
    case Some(0) => "x is 0"
    case Some(a) if (a < 0) => "x is negative"
    case Some(a) => s"x is positive: ${a}"
    //case _ => "x is positive"
  )
}

