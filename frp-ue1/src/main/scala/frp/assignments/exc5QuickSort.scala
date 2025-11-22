package frp.assignments

import java.util.concurrent.{Executors, TimeUnit}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutorService, Future}
import scala.util.{Failure, Random, Success}

given ExecutionContext = ExecutionContext.global

object exc5QuickSort:
  // #################### 1.5 ####################

  // 1.5a)
  def quickSort[T](seq: Seq[T])(using ord: Ordering[T]): Seq[T] = {
    if (seq.length <= 1) {
      seq
    } else {
      val pivot: T = seq(seq.length / 2)
      Seq.concat(
        quickSort(seq.filter(ord.lt(_, pivot))),
        seq.filter(ord.equiv(_, pivot)),
        quickSort(seq.filter(ord.gt(_ , pivot)))
      )
    }
  }

  // 1.5a)
  def testQuickSort(): Unit = {
    given Ordering[Int] = Ordering.Int

    given Ordering[Char] = Ordering.Char

    val numList: List[Int] = List.fill(20)(Random.nextInt(100))
    val charList: List[Char] = List.fill(29)(Random.nextPrintableChar())

    val resultIntList: List[Int] = quickSort(numList).toList
    val resultCharList: List[Char] = quickSort(charList).toList

    println(s"original numList: $numList")
    println(s"sorted numList: $resultIntList")
    println
    println(s"original strList: $charList")
    println(s"sorted strList: $resultCharList")
  }


  // 1.5b)
  def parallelQuickSort[T](seq: Seq[T], threshold: Int)(using ord: Ordering[T], ec: ExecutionContext): Future[Seq[T]] = {
    if (seq.length < threshold) {
      Future.successful(quickSort(seq))
    } else if (seq.length <= 1) {
      Future.successful(seq)
    } else {
      val pivot: T = seq(seq.length / 2)

      val fLeft: Future[Seq[T]] = parallelQuickSort(seq.filter(ord.lt(_, pivot)), threshold)
      val fRight: Future[Seq[T]] = parallelQuickSort(seq.filter(ord.gt(_, pivot)), threshold)

      for {
        left <- fLeft
        right <- fRight
      } yield left ++ seq.filter(ord.equiv(_, pivot)) ++ right
    }
  }

  // 1.5b)
  def measureExecutionTime[U](runs: Int)(block: => U)(using timeUnit: TimeUnit): Double = {
    val times: IndexedSeq[Long] = for (_ <- 1 to runs) yield {
      val startTime: Long = System.nanoTime()
      block
      val endTime: Long = System.nanoTime()
      timeUnit.convert(endTime - startTime, TimeUnit.NANOSECONDS)
      //(endTime - startTime).toDouble
    }

    times.sum.toDouble / runs
  }

  // 1.5b)
  def testParallelQuickSort(): Unit = {
    given Ordering[Int] = Ordering.Int

    given Ordering[Char] = Ordering.Char

    val numList: List[Int] = List.fill(20)(Random.nextInt(100))
    val charList: List[Char] = List.fill(29)(Random.nextPrintableChar())

    val fIntList: Future[Seq[Int]] = parallelQuickSort(numList, 0)
    val fCharList: Future[Seq[Char]] = parallelQuickSort(charList, 0)

    val done1 = fIntList.andThen {
      case Success(value) => {
        println(s"original numList: $numList")
        println(s"sorted numList: $value")
      }
      case Failure(ex) => println(ex)
    }

    val done2 = fCharList.andThen {
      case Success(value) => {
        println(s"original charList: $charList")
        println(s"sorted charList: $value")
      }
      case Failure(ex) => println(ex)
    }

    Await.ready(done1, Duration.Inf)
    Await.ready(done2, Duration.Inf)
  }

  // 1.5b)
  def testParallelQuickSortExecutionTimesWithThresholds(): Unit = {
    given Ordering[Int] = Ordering.Int
    given TimeUnit = TimeUnit.MICROSECONDS

    val threadPools: List[(String, ExecutionContextExecutorService)] = List(
      "Fixed-1" -> ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(1)),
      "Fixed-2" -> ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2)),
      "Fixed-4" -> ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(3)),
      "Fixed-8" -> ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8)),
      "Fixed-16" -> ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(16)),
      "Fixed-32" -> ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(32)),
      "Fixed-64" -> ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(64)),
      "Fixed-256" -> ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(256)),
      "WorkSteal" -> ExecutionContext.fromExecutorService(new java.util.concurrent.ForkJoinPool())
    )

    val numList: List[Int] = List.fill(10000)(Random.nextInt(1000000))

    println("the following Timings where measured with different threshold values:")
    println

    val normalQuickSortNs: Double = measureExecutionTime(20) {
      quickSort(numList)
    }
    println(f"normal QuickSort:    $normalQuickSortNs us")
    println

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort1Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 1)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 1:   $parQuickSort1Ns us (${tp._1})")

      tp._2.shutdown()
    }
    println

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort3Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 3)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 3:   $parQuickSort3Ns us (${tp._1})")

      tp._2.shutdown()
    }
    println

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort5Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 5)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 5:   $parQuickSort5Ns us (${tp._1})")

      tp._2.shutdown()
    }
    println

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort10Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 10)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 10:  $parQuickSort10Ns us (${tp._1})")

      tp._2.shutdown()
    }
    println

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort30Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 30)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 30:  $parQuickSort30Ns us (${tp._1})")

      tp._2.shutdown()
    }
    println

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort50Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 50)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 50:  $parQuickSort50Ns us (${tp._1})")

      tp._2.shutdown()
    }
    println

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort70Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 70)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 70:  $parQuickSort70Ns us (${tp._1})")

      tp._2.shutdown()
    }
    println()

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort80Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 80)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 80:  $parQuickSort80Ns us (${tp._1})")

      tp._2.shutdown()
    }
    println

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort90Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 90)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 90:  $parQuickSort90Ns us (${tp._1})")

      tp._2.shutdown()
    }
    println

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort100Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 100)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 100: $parQuickSort100Ns us (${tp._1})")

      tp._2.shutdown()
    }
    println

    threadPools.foreach { (tp) =>
      given ExecutionContext = tp._2

      val parQuickSort150Ns = measureExecutionTime(20) {
        val f: Future[Seq[Int]] = parallelQuickSort(numList, 150)
        Await.ready(f, Duration.Inf)
      }
      println(f"parQuickSort Th 150: $parQuickSort150Ns us (${tp._1})") // This with WorkSteal performed best on my machine. Using the default global ExecutionContext from Scala threshold 70 performed better

      tp._2.shutdown()
    }
    println
  }

  def main(args: Array[String]): Unit = {
    //testQuickSort()
    //testParallelQuickSort()
    testParallelQuickSortExecutionTimesWithThresholds()
  }
