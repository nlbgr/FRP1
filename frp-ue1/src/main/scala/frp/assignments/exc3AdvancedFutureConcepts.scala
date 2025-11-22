package frp.assignments

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Random, Success}

//given ExecutionContext = ExecutionContext.global

object exc3AdvancedFutureConcepts:
  // #################### 1.3 ####################

  // 1.3a)
  def parallelMax1(l: List[Int], n: Int): Future[Int] = {
    if (l.isEmpty) {
      return Future.failed(IllegalArgumentException("List must not be empty"))
    } else if (n <= 0) {
      return Future.failed(IllegalArgumentException(s"n must be greater than 0 but is actually $n"))
    }

    val lenOfParts: Int = l.length / n
    val sublists: List[List[Int]] = l.grouped(lenOfParts).toList
    val futures: List[Future[Int]] = sublists.map(l => Future {
      l match
        case List() => Int.MinValue
        case _ => l.max
    })

    val resultList: Future[List[Int]] = Future.sequence(futures.toSeq)
    for {
      r <- resultList
    } yield r.max
  }

  // 1.3a)
  def testParallelMax1(): Unit = {
    val validList1: List[Int] = List.fill(20)(Random.nextInt(100))
    val validList2: List[Int] = List.fill(10)(Random.nextInt(5))
    val invalidList: List[Int] = List()

    val validList1Max: Int = validList1.max
    val validList2Max: Int = validList2.max

    val f1: Future[Int] = parallelMax1(validList1, 5)
    val f2: Future[Int] = parallelMax1(validList2, 2)
    val f3: Future[Int] = parallelMax1(validList1, 15)
    val f4: Future[Int] = parallelMax1(invalidList, 5)
    val f5: Future[Int] = parallelMax1(validList1, 0)
    val f6: Future[Int] = parallelMax1(validList1, -5)

    val d1 = f1.andThen {
      case Success(value) => println(s"calc max: $value, actual max: $validList1Max")
      case Failure(ex) => println(ex)
    }

    val d2 = f2.andThen {
      case Success(value) => println(s"calc max: $value, actual max: $validList2Max")
      case Failure(ex) => println(ex)
    }

    val d3 = f3.andThen {
      case Success(value) => println(s"calc max: $value, actual max: $validList1Max")
      case Failure(ex) => println(ex)
    }

    val d4 = f4.andThen {
      case Success(value) => println(s"calc max: $value, but should be invalid")
      case Failure(ex) => println(ex)
    }

    val d5 = f5.andThen {
      case Success(value) => println(s"calc max: $value, but should be invalid")
      case Failure(ex) => println(ex)
    }

    val d6 = f6.andThen {
      case Success(value) => println(s"calc max: $value, but should be invalid")
      case Failure(ex) => println(ex)
    }

    Await.ready(d1, Duration.Inf)
    Await.ready(d2, Duration.Inf)
    Await.ready(d3, Duration.Inf)
    Await.ready(d4, Duration.Inf)
    Await.ready(d5, Duration.Inf)
    Await.ready(d6, Duration.Inf)
  }

  // 1.3b)
  def parallelMax2(l: List[Int], n: Int): Future[Int] = {
    if (l.isEmpty) {
      return Future.failed(IllegalArgumentException("List must not be empty"))
    } else if (n <= 0) {
      return Future.failed(IllegalArgumentException(s"n must be greater than 0 but is actually $n"))
    }

    val lenOfParts: Int = l.length / n
    val sublists: List[List[Int]] = l.grouped(lenOfParts).toList
    val futures: List[Future[Int]] = sublists.map(l => Future {
      l match
        case List() => Int.MinValue
        case _ => l.max
    })

    val resultList: Future[List[Int]] = futures.foldLeft(Future.successful(List[Int]()))(
      (accumulatedFuture, f) => {
        for {
          resultList <- accumulatedFuture
          newItem <- f
        } yield newItem :: resultList
      }
    )

    for {
      r <- resultList
    } yield r.max
  }

  // 1.3b)
  def testParallelMax2(): Unit = {
    val validList1: List[Int] = List.fill(20)(Random.nextInt(100))
    val validList2: List[Int] = List.fill(10)(Random.nextInt(5))
    val invalidList: List[Int] = List()

    val validList1Max: Int = validList1.max
    val validList2Max: Int = validList2.max

    val f1: Future[Int] = parallelMax2(validList1, 5)
    val f2: Future[Int] = parallelMax2(validList2, 2)
    val f3: Future[Int] = parallelMax2(validList1, 15)
    val f4: Future[Int] = parallelMax2(invalidList, 5)
    val f5: Future[Int] = parallelMax2(validList1, 0)
    val f6: Future[Int] = parallelMax2(validList1, -5)

    val d1 = f1.andThen {
      case Success(value) => println(s"calc max: $value, actual max: $validList1Max")
      case Failure(ex) => println(ex)
    }

    val d2 = f2.andThen {
      case Success(value) => println(s"calc max: $value, actual max: $validList2Max")
      case Failure(ex) => println(ex)
    }

    val d3 = f3.andThen {
      case Success(value) => println(s"calc max: $value, actual max: $validList1Max")
      case Failure(ex) => println(ex)
    }

    val d4 = f4.andThen {
      case Success(value) => println(s"calc max: $value, but should be invalid")
      case Failure(ex) => println(ex)
    }

    val d5 = f5.andThen {
      case Success(value) => println(s"calc max: $value, but should be invalid")
      case Failure(ex) => println(ex)
    }

    val d6 = f6.andThen {
      case Success(value) => println(s"calc max: $value, but should be invalid")
      case Failure(ex) => println(ex)
    }

    Await.ready(d1, Duration.Inf)
    Await.ready(d2, Duration.Inf)
    Await.ready(d3, Duration.Inf)
    Await.ready(d4, Duration.Inf)
    Await.ready(d5, Duration.Inf)
    Await.ready(d6, Duration.Inf)
  }

  // 1.3c)
  def sequenceFutures[T](futureSequence: List[Future[T]]): Future[List[T]] = {
    futureSequence.foldLeft(Future.successful(List[T]()))(
      (accumulatedFuture, f) => {
        for {
          resultList <- accumulatedFuture
          newItem <- f
        } yield newItem :: resultList
      }
    )
  }

  // 1.3c)
  def parallelMax1OwnWequenceFutures(l: List[Int], n: Int): Future[Int] = {
    if (l.isEmpty) {
      return Future.failed(IllegalArgumentException("List must not be empty"))
    } else if (n <= 0) {
      return Future.failed(IllegalArgumentException(s"n must be greater than 0 but is actually $n"))
    }

    val lenOfParts: Int = l.length / n
    val sublists: List[List[Int]] = l.grouped(lenOfParts).toList
    val futures: List[Future[Int]] = sublists.map(l => Future {
      l match
        case List() => Int.MinValue
        case _ => l.max
    })


    val resultList: Future[List[Int]] = sequenceFutures(futures)
    for {
      r <- resultList
    } yield r.max
  }

  // 1.3c)
  def testparallelMax1OwnWequenceFutures(): Unit = {
    val validList1: List[Int] = List.fill(20)(Random.nextInt(100))
    val validList2: List[Int] = List.fill(10)(Random.nextInt(5))
    val invalidList: List[Int] = List()

    val validList1Max: Int = validList1.max
    val validList2Max: Int = validList2.max

    val f1: Future[Int] = parallelMax1OwnWequenceFutures(validList1, 5)
    val f2: Future[Int] = parallelMax1OwnWequenceFutures(validList2, 2)
    val f3: Future[Int] = parallelMax1OwnWequenceFutures(validList1, 15)
    val f4: Future[Int] = parallelMax1OwnWequenceFutures(invalidList, 5)
    val f5: Future[Int] = parallelMax1OwnWequenceFutures(validList1, 0)
    val f6: Future[Int] = parallelMax1OwnWequenceFutures(validList1, -5)

    val d1 = f1.andThen {
      case Success(value) => println(s"calc max: $value, actual max: $validList1Max")
      case Failure(ex) => println(ex)
    }

    val d2 = f2.andThen {
      case Success(value) => println(s"calc max: $value, actual max: $validList2Max")
      case Failure(ex) => println(ex)
    }

    val d3 = f3.andThen {
      case Success(value) => println(s"calc max: $value, actual max: $validList1Max")
      case Failure(ex) => println(ex)
    }

    val d4 = f4.andThen {
      case Success(value) => println(s"calc max: $value, but should be invalid")
      case Failure(ex) => println(ex)
    }

    val d5 = f5.andThen {
      case Success(value) => println(s"calc max: $value, but should be invalid")
      case Failure(ex) => println(ex)
    }

    val d6 = f6.andThen {
      case Success(value) => println(s"calc max: $value, but should be invalid")
      case Failure(ex) => println(ex)
    }

    Await.ready(d1, Duration.Inf)
    Await.ready(d2, Duration.Inf)
    Await.ready(d3, Duration.Inf)
    Await.ready(d4, Duration.Inf)
    Await.ready(d5, Duration.Inf)
    Await.ready(d6, Duration.Inf)
  }


  def main(args: Array[String]): Unit = {
    //testParallelMax1()
    //testParallelMax2()
    testparallelMax1OwnWequenceFutures()
  }