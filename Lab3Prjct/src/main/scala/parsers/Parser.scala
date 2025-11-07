package parsers

trait Result[+T]:
  val rest: String

case class Success[+T](result: T, rest: String) extends Result[T]

case class Failure(message: String, rest: String) extends Result[Nothing]

trait Parser[+T] extends (String => Result[T]) {
  thisParser: Parser[T] =>

  // inherited def apply(input: String): Result[T]

  def flatMap[R](f: T => Parser[R]): Parser[R] =
    input =>
      thisParser(input) match {
        case Success(t, rest) => f(t)(rest)
        case f@Failure(_, _) => f
      }

  def map[R](f: T => R): Parser[R] =
    input =>
      thisParser(input) match {
        case Success(t, rest) => Success(f(t), rest)
        case f@Failure(_, _) => f
      }

  def filter(pred: T => Boolean): Parser[T] =
    input =>
      thisParser(input) match {
        case s@Success(r, rest) if pred(r) => s
        case Success(_, _) => Failure(s"Filter not successful", input)
        case f: Failure => f
      }

  def ~[U](sndParser: => Parser[U]): Parser[(T, U)] =
    flatMap(t => sndParser.map(r => (t, r)))

  def ~>[U] (sndParser: => Parser[U]): Parser[U] = (this ~ sndParser).map((t, u) => u)

  def <~[U](sndParser: => Parser[U]): Parser[T] = (this ~ sndParser).map((t, u) => t)

//    def ~[U](sndParser: => Parser[U]): Parser[(T, U)] =
//      input => {
//        thisParser(input) match {
//          case Success(r1, rest1) => {
//            sndParser(rest1) match {
//              case Success(r2, rest2) => Success((r1, r2), rest2)
//              case Failure(msg, _) => Failure(msg, input)
//            }
//          }
//          case f@Failure(_, _) => f
//        }
//      }

  def |[U >: T](otherParser: => Parser[U]): Parser[U] = {
    input => {
      thisParser(input) match {
        case s@Success(r, rest) => s
        case Failure(_, _) => otherParser(input)
      }
    }
  }

  def opt: Parser[Option[T]] =
    input => {
      thisParser(input) match {
        case Success(r, rest) => Success(Some(r), rest)
        case Failure(_, rest) => Success(None, input)
      }
    }


  def rep: Parser[List[T]] =
    new Parser[List[T]] {
      listParser => 
      override def apply(input: String): Result[List[T]] = {
        thisParser(input) match {
          case Success(r, rest) => 
            listParser(rest) match
              case Success(rList, restList) => Success(r::rList, restList)
              case _ => Failure("does not happen", rest)
          
          case Failure(_, rest) => Success(Nil, rest)
        }
      }
    }
}

