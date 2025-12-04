package frp.basics.iot

import org.apache.pekko.NotUsed
import org.apache.pekko.actor.typed.scaladsl.AskPattern.*
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem}
import org.apache.pekko.stream.scaladsl.Flow
import org.apache.pekko.util.Timeout
import frp.basics.DefaultActorSystem
import frp.basics.iot.DatabaseActor
import frp.basics.iot.DatabaseActor.Insert
import frp.basics.MeasureUtil.measure
import org.slf4j.LoggerFactory

import scala.annotation.unused
import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration}
import scala.concurrent.{Await, ExecutionContext, Future}

val NR_MESSAGES = 200
val MESSAGES_PER_SECOND = 100
val PARALLELISM = Runtime.getRuntime.availableProcessors
val BULK_SIZE = 5
val TIME_WINDOW = 200.millis

@main
def ioTApp(): Unit =

  //
  // Identity Service
  // ----------------
  // A trivial pass-through Flow that simply returns the input strings as output.
  // Useful for testing the message handling infrastructure without any processing.
  //
  def identityService(): Flow[String, String, NotUsed] = ???

  //
  // Simple Measurements Service
  // ---------------------------
  // Processes JSON messages into Measurement objects, inserts them individually into the repository,
  // and converts the acknowledgement into JSON. Backpressure and parallelism are handled by mapAsync.
  //
  def simpleMeasurementsService(parallelism: Int = PARALLELISM)(using ec: ExecutionContext): Flow[String, String, NotUsed] = ???

  //
  // Measurements Service Using Actor
  // --------------------------------
  // Uses a DatabaseActor to handle inserts. Each measurement is sent via the ask pattern and
  // the acknowledgement is awaited asynchronously. Bulk size and parallelism control throughput.
  //
  def measurementsServiceWithActor(dbActor: ActorRef[DatabaseActor.Command], parallelism: Int = PARALLELISM, bulkSize: Int = BULK_SIZE)
                                  (using actorSystem: ActorSystem[?], timeout: Timeout, ec: ExecutionContext):
      Flow[String, String, NotUsed] = ???

  //
  // Bulk Measurements Service
  // -------------------------
  // Collects measurements in batches (groupedWithin) and performs bulk inserts into the repository.
  // Each acknowledgement from the repository is converted into JSON. Parallelism controls concurrency.
  //
  def measurementsService(parallelism: Int = PARALLELISM, bulkSize: Int = BULK_SIZE, timeWindow: FiniteDuration = TIME_WINDOW)
                         (using ec: ExecutionContext): Flow[String, String, NotUsed] = ???

  //
  // Main Application
  // ----------------
  // Initializes the actor system, creates the DatabaseActor, and sets up the server simulator
  // to send messages through the selected Flow. Measures throughput using the MeasureUtil.
  //



  //
  // Terminate the actor system
  //
  Await.ready(DefaultActorSystem.terminate(), Duration.Inf)
end ioTApp