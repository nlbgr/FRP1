package parsers

object ParserDemos {

  def main(args: Array[String]): Unit = {

    val xParser: Parser[Char] = char('x')
    val yParser: Parser[Char] = char('y')
    val zParser: Parser[Char] = char('z')

    // sequences

    val xyzParser: Parser[((Char, Char), Char)] = xParser ~ yParser ~ zParser

    val xyzR: Result[((Char, Char), Char)] = xyzParser("xyzuv")
    println(s"xyzR = $xyzR")

    def inBracketsParser[T](parser: Parser[T]) = char('(') ~>  parser <~ char(')')

    val xyzInBrackets = inBracketsParser(xyzParser)("(xyz)")
    println(s"xyzInBrackets = $xyzInBrackets")

    // alternative

    val u_Or_vParser: Parser[Char] = char('u') | char('v')

    val u_Or_vResult = u_Or_vParser("ux")
    println(s"u_Or_vResult = $u_Or_vResult") // Success('u', "x")

    val u_Or_vResult2 = u_Or_vParser("vx")
    println(s"u_Or_vResult2 = $u_Or_vResult2") // Success('v', "x")

    val u_Or_vxParser: Parser[(Char, Char)] = (char('u') | char('v')) ~ char('x')

    val u_Or_vxResult = u_Or_vxParser("ux")
    println(s"u_Or_vxResult = $u_Or_vxResult") // Success(('u', 'x'); "")

    val u_Or_vxResult2 = u_Or_vxParser("vx")
    println(s"u_Or_vxResult2 = $u_Or_vxResult2") // Success(('v', 'x'); "")

    // optional

    val opt_u_Parser: Parser[Option[Char]] = char('u').opt

    val opt_u_Result = opt_u_Parser("ux")
    println(s"opt_u_Result = $opt_u_Result") // Success(Some(u),x)

    val opt_u_Result2 = opt_u_Parser("x")
    println(s"opt_u_Result2 = $opt_u_Result2") // Success(None,x)

    val opt_u_xParser: Parser[(Option[Char], Char)] = char('u').opt ~ char('x')

    val opt_u_xResult = opt_u_xParser("ux")
    println(s"opt_u_xResult = $opt_u_xResult") // Success((Some(u),x),)

    val opt_u_xResult2 = opt_u_xParser("x")
    println(s"opt_u_xResult2 = $opt_u_xResult2") // Success((None,x),)

    // repetition

    val rep_xParser: Parser[List[Char]] = char('x').rep

    val rep_xResult = rep_xParser("xxxxv")
    println(s"rep_xResult = $rep_xResult") // Success(List(x, x, x, x),v)

    val rep_xResult2 = rep_xParser("v")
    println(s"rep_xResult2 = $rep_xResult2") // Success(List(),v)


    // example combination

    val u_Or_vxyParser = (char('u') | char('v')) ~ char('x').opt ~ char('y')
    val u_Or_vxyResult = u_Or_vxyParser("uxy")
    println(s"u_Or_vxyResult = $u_Or_vxyResult")

    val u_Or_vxyyyyParser = (char('u') | char('v')) ~ char('x').opt ~ char('y').rep ~ char('z')
    val u_Or_vxyyyyResult = u_Or_vxyyyyParser("vxyyyyz")
    println(s"u_Or_vxyyyyResult = $u_Or_vxyyyyResult")

    val xyParser = char('x') ~ char('y')

    val xyR = xyParser("xa")
    println(s"xyR = $xyR")

  }
}

