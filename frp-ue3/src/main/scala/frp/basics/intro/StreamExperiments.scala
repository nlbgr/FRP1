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
  
  def computePrimes(): Unit = ()

  def computePrimesMat(): Unit = ()

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

  def sequentialStream(): Unit = ()

  def asyncBoundaryStream(): Unit = ()

  def asyncBoundaryStreamTraced(): Unit = ()

  def mapAsyncStream(): Unit = ()

  def mapAsyncStreamTraced(): Unit = ()

  def computePrimesMapAsync(): Unit = ()


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
  
  println("========== computePrimesMapAsync =============")
  computePrimesMapAsync()

end streamingExperiments
