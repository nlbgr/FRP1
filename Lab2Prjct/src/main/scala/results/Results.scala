package results

import java.nio.file.{Files, Paths}
import scala.io.Source
import scala.language.postfixOps

case class Results(id: Int, name: String, points: IndexedSeq[Int])

enum Grade :
  case EXCELLENT, GOOD, SATISFACTORY, SUFFICIENT, INSUFFICIENT

object ResultsAnalysis {

  def main(args: Array[String]): Unit = {

    val lines: List[String] = Source.fromFile("files/results.csv").getLines.toList
    println(lines.mkString("\n"))

    // Task 4.1: List of Results objects

    val resultList : List[Results] = lines.drop(1).map(l => {
      val elems = l.split(",").map(_.trim)

      Results(elems(0).toInt, elems(1), elems.drop(2).map(_.toInt))
    })
    println(resultList.mkString("\n"))

    // Task 4.2: Number of solved tasks

    val nSolvedPerStnd : Map[String, Int] = resultList.map(r => {
      (r.name, r.points.count(p => p >= 3))
    }).toMap()
    println(nSolvedPerStnd)


    // Task 4.3: Sufficient tasks solved

    val sufficientSolved : (Set[String], Set[String]) = {
      val (ok, no) = nSolvedPerStnd.partition((_, points) => points >= 8)
      (ok.keySet, no.keySet)
    }
    println(sufficientSolved)

    // Task 4.4: Grading

    val grades : Map[String, Grade] = resultList.map(r => (r.name, computeGrade(r.points))).toMap
    println(grades)

    // Task 4.5: Grade statistics

    val nStudentsWithGrade : Map[Grade, Int] = grades.groupBy((name, grade) => grade).map((grade, names) => (grade, names.size)).toMap
    println(nStudentsWithGrade)

    // Task 4.6: Number solved per assignment

    val nSolvedPerAssnmt : List[(Int, Int)] =
      (1 to 10).map(i => (i, resultList.map(_.points(i-1)).count(p => p >= 3))).toList
    println(nSolvedPerAssnmt)

    // Task 4.7.: Average points per assignment

    val avrgPointsPerAssnmt : List[(Int, Double)] =
      (1 to 10).map(
        i => (i, {
          val points = resultList.map(_.points(i-1)).filter(p => p >= 0)
          points.sum.toDouble / points.size
        })
      ).toList
    println(avrgPointsPerAssnmt)

  }

  private def computeGrade(points: IndexedSeq[Int]): Grade = {
    if (points.count(p => p >= 3) < 8) then Grade.INSUFFICIENT
    else {
      val avrg = points.sorted.drop(2).sum.toDouble / 8
      if (avrg < 5.0) then Grade.INSUFFICIENT
      else if (avrg < 6.5) then Grade.SUFFICIENT
      else if (avrg < 8.0) then Grade.SATISFACTORY
      else if (avrg < 9.0) then Grade.GOOD
      else Grade.EXCELLENT
    }
  }

}
