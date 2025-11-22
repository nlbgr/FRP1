package frp.assignments

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Random, Success}

//given ExecutionContext = ExecutionContext.global


object exc1FutureBaseis:
  // #################### 1.2 ####################

  // 1.2a)
  def doInParallel(block1: => Unit, block2: => Unit): Future[Unit] = {
    val f1: Future[Unit] = Future(block1)
    val f2: Future[Unit] = Future(block2)

    val res: Future[Unit] = for {
      _ <- f1
      _ <- f2
    } yield ()

    res
  }

  // 1.2a)
  def testDoInParallel1(): Unit = {
    // just waste time in the blocks in order to test

    val f1: Future[Unit] = doInParallel({
      for(i <- 1 to 5) { }
    },{
      for (i <- 1 to 5) { }
    })

    val f2: Future[Unit] = doInParallel({
      for (i <- 1 to 5) { }
    }, {
      throw Exception("f2-test block 2")
    })

    val f3: Future[Unit] = doInParallel({
      throw Exception("f3-test block 1")
    }, {
      for (i <- 1 to 5) { }
    })

    val d1 = f1.andThen {
      case Success(value) => println("f1 success")
      case Failure(ex) => println(s"ex1: $ex")
    }
    val d2 = f2.andThen {
      case Success(value) => println("f2 success")
      case Failure(ex) => println(s"ex2: $ex")
    }
    val d3 = f3.andThen {
      case Success(value) => println("f3 success")
      case Failure(ex) => println(s"ex3: $ex")
    }


    Await.ready(d1, Duration.Inf)
    Await.ready(d2, Duration.Inf)
    Await.ready(d3, Duration.Inf)
  }

  // 1.2b)
  def doInParallel[U, V](f1: Future[U], f2: Future[V]): Future[(U, V)] = {
    val res: Future[(U, V)] = for {
      r1 <- f1
      r2 <- f2
    } yield (r1, r2)

    res
  }

  // 1.2b)
  def doInParallelFlapMap[U, V](f1: Future[U], f2: Future[V]): Future[(U, V)] = {
    val res: Future[(U, V)] = f1.flatMap(r1 => {
      f2.map(r2 => {
        (r1, r2)
      })
    })

    res
  }

  // 1.2b)
  def testDoInParallel2(): Unit = {
    val f1input: Future[Int] = Future.successful(1)
    val f2input: Future[Double] = Future.successful(2.0)
    val f3input: Future[Float] = Future.failed(Exception("test failure"))

    val f1: Future[(Int, Double)] = doInParallel(f1input, f2input)
    val f2: Future[(Double, Int)] = doInParallelFlapMap(f2input, f1input)
    val f3: Future[(Float, Double)] = doInParallel(f3input, f2input)
    val f4: Future[(Int, Float)] = doInParallelFlapMap(f1input, f3input)

    val d1 = f1.andThen {
      case Success(value) => println(s"success1: $value")
      case Failure(ex) => println(s"ex1: $ex")
    }

    val d2 = f2.andThen {
      case Success(value) => println(s"success2: $value")
      case Failure(ex) => println(s"ex2: $ex")
    }

    val d3 = f3.andThen {
      case Success(value) => println(s"success3: $value")
      case Failure(ex) => println(s"ex3: $ex")
    }

    val d4 = f4.andThen {
      case Success(value) => println(s"success4: $value")
      case Failure(ex) => println(s"ex4: $ex")
    }

    Await.ready(d1, Duration.Inf)
    Await.ready(d2, Duration.Inf)
    Await.ready(d3, Duration.Inf)
    Await.ready(d4, Duration.Inf)
  }

  // 1.2c)
  def doInParallelZip[U, V](f1: Future[U], f2: Future[V]): Future[(U, V)] = {
    f1.zip(f2)
  }

  // 1.2c)
  def testDoInParallel3(): Unit = {
    val f1input: Future[Int] = Future.successful(1)
    val f2input: Future[Double] = Future.successful(2.0)
    val f3input: Future[Float] = Future.failed(Exception("test failure"))

    val f1: Future[(Int, Double)] = doInParallelZip(f1input, f2input)
    val f2: Future[(Float, Double)] = doInParallelZip(f3input, f2input)

    val d1 = f1.andThen {
      case Success(value) => println(s"success1: $value")
      case Failure(ex) => println(s"ex1: $ex")
    }

    val d2 = f2.andThen {
      case Success(value) => println(s"success2: $value")
      case Failure(ex) => println(s"ex2: $ex")
    }

    Await.ready(d1, Duration.Inf)
    Await.ready(d2, Duration.Inf)
  }

  // 1.2d)
  // Annahme: nachdem es in der Angabe keinen O(n) Merge Algorithmus von sortierten Listen gibt
  // (obwohl darauf in 1.2d) referenziert wird, wird einer implementiert
  def mergeTwoSortedLists[T](l1: List[T], l2: List[T], pred: (T, T) => Boolean): List[T] = {
    def mergeTwoSortedListsRec[T](l1: List[T], l2: List[T], pred: (T, T) => Boolean, res: List[T]): List[T] = {
      (l1, l2) match
        case (Nil, _) => res ++ l2
        case (_, Nil) => res ++ l1
        case (head1 :: tail1, head2 :: tail2) => {
          if (pred(head1, head2)) {
            mergeTwoSortedListsRec(tail1, l2, pred, res ++ List(head1))
          } else {
            mergeTwoSortedListsRec(l1, tail2, pred, res ++ List(head2))
          }
        }
    }

    mergeTwoSortedListsRec(l1, l2, pred, Nil)
  }

  // 1.2d)
  def testDoInParallel4(): Unit = {
    val randomList: List[Int] = List.fill[Int](20)(Random.nextInt(500))
    println(s"randomList: $randomList")

    val (listLeft, listRight) = randomList.splitAt(10)
    println(s"leftRandomList: $listLeft")
    println(s"rightRandomList: $listRight")

    val sortingRule: (Int, Int) => Boolean = (a, b) => a < b
    val fSorting = doInParallelZip(
      Future { listLeft.sortWith(sortingRule) },
      Future { listRight.sortWith(sortingRule) }
    )

    val (leftSorted, rightSorted) = Await.result(fSorting, Duration.Inf)
    println(s"leftSortedList: $leftSorted")
    println(s"rightSortedList: $rightSorted")

    val sortedList = mergeTwoSortedLists(leftSorted, rightSorted, sortingRule)
    println(s"sortedList: $sortedList")
  }

  def main(args: Array[String]): Unit = {
    //testDoInParallel1()
    //testDoInParallel2()
    //testDoInParallel3()
    testDoInParallel4()
  }