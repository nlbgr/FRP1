package frp.assignments

import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

//given ExecutionContext = ExecutionContext.global

object exc4Promises:
  // #################### 1.4 ####################

  // 1.4a)
  def testFutureFirstCompletedOf(): Unit = {
    // We have 3 different servers
    // all 3 have different latencies.
    // To determine which is the fastest do download an artifact from
    // we check for the fastest connection before starting the download
    // from the fastest
    val downloadServers: List[Future[String]] = List(1 -> 450, 2 -> 300, 3 -> 500/*, 4 -> -1*/).map((id, ltc) => Future {
      if (ltc <= 0) {
        throw Exception(s"server $id not found") // was used to test if exceptions also complete the future, which is the case
      } else {
        Thread.sleep(ltc)
        s"Hello from server $id with latency $ltc"
      }
    })

    val fastestServer: Future[String] = Future.firstCompletedOf(downloadServers)
    val done = fastestServer.andThen {
      case Success(value) => println(value)
      case Failure(ex) => println(ex)
    }

    Await.ready(done, Duration.Inf)
  }

  // 1.4b)
  extension (f: Future.type)
    def doCompetitively[T](futureList: List[Future[T]]): Future[T] = {
      if (futureList.isEmpty) {
        Future.never
      } else {
        val p: Promise[T] = Promise()
        futureList.foreach(future => {
          future.onComplete(res => p.tryComplete(res)) // tryComplete has single-assignment semantic according to docs => already threadsafe (https://docs.scala-lang.org/overviews/core/futures.html#promises)
        })

        p.future
      }
    }

  // 1.4b)
  def testFutureExtensionDoCompetitively(): Unit = {
    // We have 3 different servers
    // all 3 have different latencies.
    // To determine which is the fastest do download an artifact from
    // we check for the fastest connection before starting the download
    // from the fastest
    val downloadServers: List[Future[String]] = List(1 -> 450, 2 -> 300, 3 -> 500).map((id, ltc) => Future {
      Thread.sleep(ltc)
      s"Hello from server $id with latency $ltc"
    })

    val fastestServer: Future[String] = Future.doCompetitively(downloadServers)
    val done = fastestServer.andThen {
      case Success(value) => println(value)
      case Failure(ex) => println(ex)
    }

    Await.ready(done, Duration.Inf)
  }

  def main(args: Array[String]): Unit = {
    //testFutureFirstCompletedOf()
    testFutureExtensionDoCompetitively()
  }
