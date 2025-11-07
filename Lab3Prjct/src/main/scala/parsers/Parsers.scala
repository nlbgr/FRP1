package parsers

private def skipWhiteSpaces(str: String): String = {
  str.substring(str.indexWhere(!Character.isWhitespace(_)))
}

val anyChar : Parser[Char] =
  inp =>
    if inp.isEmpty then Failure("Empty input", inp)
    else Success(inp.charAt(0), inp.substring(1))

def char(c: Char): Parser[Char] = anyChar.filter(r => r == c)

def oneOutOf[E](pe: Parser[E], outOf: List[E]): Parser[E] =
  pe.filter(r => outOf.contains(r))

def charOutOf(cs: List[Char]) = oneOutOf(anyChar, cs)

val letter = charOutOf("abcdefghijklmnopqrstuvwxyz".toList)

val digit = charOutOf("0123456789".toList)


val anyWord: Parser[String] =
  inp => {
    val tokenParser = (letter | digit).rep.map(lstChars => String.valueOf(lstChars.toArray[Char]))
    tokenParser(skipWhiteSpaces(inp))
  }

def word(w: String): Parser[String] =
  anyWord.filter(_ == w)

def some(s: String): Parser[String] =
  inp =>
    if (s.length == 0) Success("", inp)
    else char(s.charAt(0))(s) match {
      case Success(_, rest) => some(s.substring(1))(rest)
      case Failure(msg, _) => Failure(msg, inp)
    }

val int: Parser[Int] = anyWord.filter(tk => tk.forall(c => c.isDigit)).map { ds => ds.toInt }

val bool: Parser[Boolean] = anyWord.filter(t => t == "true").map(_ => true) |
  anyWord.filter(t => t == "false").map(_ => false)

val ident: Parser[String] = anyWord.filter(t => t.length > 0 && t.charAt(0).isLetter)
