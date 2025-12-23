package frp.assignments.task3

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import scala.concurrent.duration.*

object IotStreamProcessing:
  @main
  def main(): Unit = {
    val system: ActorSystem[WeatherStation.Command] = ActorSystem(
      Behaviors.setup { context =>
        //val dataStorage = context.spawn(DataStorageStreamIo("test.csv"), "dataStore")
        val dataStorageRouter = context.spawn(DataStorageRouter(5), "routerActor")
        val weatherStation = context.spawn(
          //WeatherStation(10.millis, -10.0, 40.0, dataStorage),
          WeatherStation(10.millis, -10.0, 40.0, dataStorageRouter),
          "station"
        )

        weatherStation ! WeatherStation.GenerateMeasurements

        Behaviors.same
      }, "system")
  }
