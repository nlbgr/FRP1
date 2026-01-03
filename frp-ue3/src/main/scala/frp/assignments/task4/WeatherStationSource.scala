package frp.assignments.task4

import org.apache.pekko.stream.scaladsl.Source

import java.time.Instant
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.Random

object WeatherStationSource:
  def apply(
    interval: FiniteDuration,
    minTemperature: Double,
    maxTemperature: Double,         
  ): Source[Measurement, _] = 
    Source
      .tick(initialDelay = Duration.Zero, interval = interval, tick = ())
      .map { _ =>
        val temp: Double = Random.nextDouble() * (maxTemperature - minTemperature) + minTemperature
        val time = Instant.now()
        println(s"produce: {$time, $temp}, ${Thread.currentThread.threadId}")
        
        Measurement(time, temp)
      }
