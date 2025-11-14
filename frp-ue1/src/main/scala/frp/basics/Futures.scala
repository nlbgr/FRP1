package frp.basics

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Random
import scala.util.{Failure, Success}
//import scala.concurrent.ExecutionContext.Implicits.global // Scala 2

given ExecutionContext = ExecutionContext.global // Scala 3

// noinspection Duplicates
object Futures:

  private def println(x: Any): Unit = Console.println(s"$x (thread id=${Thread.currentThread.threadId})")

  private def doWork(task: String, steps: Int): Unit =
    for (i <- 1 to steps)
      println(s"$task: $i")
      if (i == 6) throw new IllegalArgumentException()
      Thread.sleep(200)
  end doWork

  private def compute(task : String, n: Int) : Int =
    for (i <- 1 to n)
      println(s"$task: $i")
      Thread.sleep(200)
    Random.nextInt(1000)
  end compute

  private def combine(value1 : Int, value2 : Int) : Int =
    for (i <- 1 to 5)
      println(s"combine: $i")
      Thread.sleep(200)
    value1 + value2
  end combine

  private def combineAsync(value1: Int, value2: Int): Future[Int] = Future {
    for (i <- 1 to 5)
      println(s"combine: $i")
      Thread.sleep(200)
    value1 + value2
  }

  private def sequentialInvocation(): Unit = {
    doWork("task-1", 5)
    doWork("task-2", 5)
  }

  private def simpleFutures(): Unit = {
    val f1: Future[Unit] = Future { doWork("task-1", 5) } // Future.apply(<call by Name>)(<impliziter Executor durch import oder given>)
    val f2: Future[Unit] = Future { doWork("task-2", 5) }

    // Nur zum Testen
    Await.ready(f1, Duration.Inf)
    Await.ready(f2, Duration.Inf)
  }

  private def futuresWithCallback(): Unit = {
    val f1 = Future { compute("task-1", 5) }
    val f2 = Future { compute("task-2", 6) }

    f1.foreach(r1 => println(s"r1 -> $r1")) // foreach wird in anderem Thread durchgeführt, daher aufpassen
    f2.onComplete { // Partielle Funktion
      case Success(r2) => println(s"r2 -> $r2")
      case Failure(ex2) => println(s"ex2 -> $ex2")
    }

    // Nur zum Testen
    Await.ready(f1, Duration.Inf)
    Await.ready(f2, Duration.Inf)
    Thread.sleep(50)
  }

  private def futureComposition1(): Unit = {
    val f1 = Future { compute("task-1", 5) }
    val f2 = Future { compute("task-2", 6) }

    val res: Future[Int] = for {
      r1 <- f1
      r2 <- f2
    } yield combine(r1, r2)

    val done = res.andThen {
      case Success(r) => println(s"res -> $r")
      case Failure(ex) => println(s"ex -> $ex")
    }

    // Nur zum Testen
    Await.ready(done, Duration.Inf)
  }

  private def futureComposition2(): Unit = {
    val f1 = Future { compute("task-1", 5) }
    val f2 = Future { compute("task-2", 6) }

    val res: Future[Int] = for {
      r1 <- f1
      r2 <- f2
      res <- combineAsync(r1, r2)
    } yield res

    val done = res.andThen {
      case Success(r) => println(s"res -> $r")
      case Failure(ex) => println(s"ex -> $ex")
    }

    // Nur zum Testen
    Await.ready(done, Duration.Inf)
  }

  def main(args: Array[String]): Unit =
    println(s"availableProcessors=${Runtime.getRuntime.availableProcessors}")

    //println("==== sequentialInvocation ====")
    //sequentialInvocation()

    //println("\n==== simpleFutures ====")
    //simpleFutures()

    //println("\n==== futuresWithCallback ====")
    //futuresWithCallback()

    //println("\n==== futureComposition1 ====")
    //futureComposition1()

    println("\n==== futureComposition2 ====")
    futureComposition2()
  end main

end Futures

