package frp.basics.intro

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import org.apache.pekko.{Done, NotUsed}
import frp.basics.DefaultActorSystem
import frp.basics.LogUtil.traceWithThreadId
import frp.basics.PrimeUtil.isPrime
import org.slf4j.LoggerFactory

import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.jdk.DurationConverters.*

@main
def streamingExperiments(): Unit =

  LoggerFactory.getLogger("bootstrap")
  val actorSystem = DefaultActorSystem()
  given ActorSystem[?] = actorSystem
  given ExecutionContext = actorSystem.executionContext
  
  def computePrimes(): Unit = {
    val source: Source[Int, NotUsed] = Source(2 to 200)
    val flow: Flow[Int, Int, NotUsed] = Flow[Int].filter(isPrime)
    val sink: Sink[Int, Future[Done]] = Sink.foreach[Int](n => print(s"$n "))

//    val stage1: Source[Int, NotUsed] = source.via(flow)
//    val stage2: Flow[Int, Nothing, NotUsed] = flow.via(flow)
//    val stage3: Sink[Int, NotUsed] = flow.to(sink)

    val graph: RunnableGraph[NotUsed] = source.via(flow).to(sink)
    graph.run()

    Thread.sleep(50)
    println()
  }

  def computePrimesMat(): Unit = {
    val source: Source[Int, NotUsed] = Source(2 to 200)
    val flow: Flow[Int, Int, NotUsed] = Flow[Int].filter(isPrime)
    val sink: Sink[Int, Future[Done]] = Sink.foreach[Int](n => print(s"$n "))

    val graph1: RunnableGraph[Future[Done]] = source.via(flow).toMat(sink)((l, r) => r)
    val graph2: RunnableGraph[Future[Done]] = source.via(flow).toMat(sink)(Keep.right)

    val done: Future[Done] = graph2.run()
    Await.ready(done, Duration.Inf)
    println()
  }

  val itemSource = Source(1 to 10)
  val itemSink = Sink.foreach[Int](i => print(s"$i "))

  def produce(item: Int, time: FiniteDuration, trace: Boolean = false) =
    if (trace) traceWithThreadId(s"produce($item)")
    Thread.sleep(time.toMillis)
    item

  def consume(item: Int, time: FiniteDuration, trace: Boolean = false) =
    if (trace) traceWithThreadId(s"consume($item)")
    Thread.sleep(time.toMillis)
    item

  def sequentialStream(): Unit = {
    val done: Future[Done] = itemSource
      .map(n => produce(n, 500.millis))
      .map(n => consume(n, 500.millis))
      .runWith(itemSink)

    Await.ready(done, Duration.Inf)
    println()
  }

  def asyncBoundaryStream(): Unit = {
    val done: Future[Done] = itemSource
      .map(n => produce(n, 500.millis))
      .async
      .map(n => consume(n, 500.millis))
      .runWith(itemSink)

    Await.ready(done, Duration.Inf)
    println()
  }

  def asyncBoundaryStreamTraced(): Unit = {
    val done: Future[Done] = itemSource
      .map(n => produce(n, 500.millis, trace = true))
      .async
      .map(n => consume(n, 500.millis, trace = true))
      //.runWith(Sink.ignore)
      .run()

    Await.ready(done, Duration.Inf)
    println()
  }

  def mapAsyncStream(): Unit = {
    val done: Future[Done] = itemSource
      .mapAsync(3)(n => Future { produce(n, 500.millis) })
      .mapAsync(3)(n => Future { consume(n, 500.millis) })
      .runWith(itemSink)

    Await.ready(done, Duration.Inf)
    println()
  }

  def mapAsyncStreamTraced(): Unit = {
    val done: Future[Done] = itemSource
      .mapAsync(3)(n => Future {
        produce(n, 500.millis, trace = true)
      })
      .mapAsync(3)(n => Future {
        consume(n, 500.millis, trace = true)
      })
      .run()

    Await.ready(done, Duration.Inf)
    println()
  }

  def computePrimesMapAsync1(): Unit = {
    val done = Source(2 to 200)
      .mapAsync(3)(n => Future {
        (n, isPrime(n))
      })
      .filter((_, prime) => prime)
      .map((n, _) => n)
      .runWith(Sink.foreach[Int](p => print(s"$p ")))

    Await.ready(done, Duration.Inf)
    println()
  }

  def computePrimesMapAsync2(): Unit = {
    val done = Source(2 to 200)
      .mapAsync(3)(n => Future {
        (n, isPrime(n))
      })
      .collect {
        case (n, true) => n
      }
      .runWith(Sink.foreach[Int](p => print(s"$p ")))

    Await.ready(done, Duration.Inf)
    println()
  }


  //
  // main
  //

  println("================ computePrimes ===============")
  computePrimes()

  println("============== computePrimesMat ==============")
  computePrimesMat()

  println("============= sequentialStream ===============")
  sequentialStream()

  println("============ asyncBoundaryStream =============")
  asyncBoundaryStream()

  println("========= asyncBoundaryStreamTraced ==========")
  asyncBoundaryStreamTraced()

  println("============== mapAsyncStream ================")
  mapAsyncStream()

  println("=========== mapAsyncStreamTraced =============")
  mapAsyncStreamTraced()

  println("========== computePrimesMapAsync1 =============")
  computePrimesMapAsync1()

  println("========== computePrimesMapAsync2 =============")
  computePrimesMapAsync2()

  // Terminate ActorSystem
  actorSystem.terminate()
end streamingExperiments
