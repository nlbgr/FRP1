package frp.assignments.task4

import frp.basics.DefaultActorSystem
import org.apache.pekko.Done
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.stream.scaladsl.Sink

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.*

object IoTStream:
  @main
  def main(): Unit = {
    val actorSystem = DefaultActorSystem()

    given ActorSystem[?] = actorSystem
    given ExecutionContext = ExecutionContext.global

//    val done: Future[Done] =
//      WeatherStationSource(10.millis, -10.0, 40.0)
//        .via(DataStorageFlow(filePath = "streams.csv"))
//        .run()

    val done: Future[Done] =
      WeatherStationSource(10.millis, -10.0, 40.0).async
        .via(DataStorageFlow(filePath = "streams.csv")).async
        .run()

    Await.ready(done, Duration.Inf)
  }
