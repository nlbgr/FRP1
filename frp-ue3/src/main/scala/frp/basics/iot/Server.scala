package frp.basics.iot

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.{Done, NotUsed}
import org.apache.pekko.stream.scaladsl.{Flow, Sink, Source}
import frp.basics.DefaultActorSystem
import frp.basics.LogUtil.tracef

import java.time.LocalDateTime
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random

trait Server:

end Server

object ServerSimulator:
  def apply(nrMessages: Int = Int.MaxValue, messagesPerSecond: Int = 20) =
    new ServerSimulator(nrMessages, messagesPerSecond)
end ServerSimulator


class ServerSimulator(nrMessages: Int, messagesPerSecond: Int) extends Server:
  given ActorSystem[?] = DefaultActorSystem()
  private var tracingEnabled = false;
  
  def withTracing(enabled: Boolean = true): ServerSimulator =
    tracingEnabled = enabled
    this
  end withTracing
end ServerSimulator