package frp.assignments.task3

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors

import java.time.Instant
import scala.concurrent.duration.FiniteDuration
import scala.util.Random

object WeatherStation:
  sealed trait Command
  case object GenerateMeasurements extends Command

  def apply(
    interval: FiniteDuration,
    minTemperature: Double,
    maxTemperature: Double,
    measurementProcessor: ActorRef[Measurement]
  ): Behavior[Command] =
    Behaviors.withTimers { timers =>
      timers.startTimerAtFixedRate(GenerateMeasurements, interval)

      Behaviors.receive { (_, command) =>
        command match
          case GenerateMeasurements =>
            val temp: Double = Random.nextDouble() * (maxTemperature - minTemperature) + minTemperature
            val time = Instant.now()
            println(s"produce: {$time, $temp}, ${Thread.currentThread.threadId}")
            measurementProcessor ! Measurement(time, temp)
            Behaviors.same
      }
    }