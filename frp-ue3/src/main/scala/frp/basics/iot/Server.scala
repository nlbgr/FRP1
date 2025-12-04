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
  def handleMessages(service: Flow[String, String, NotUsed]): Future[Done]
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

  override def handleMessages(service: Flow[String, String, NotUsed]): Future[Done] = {
    val source = Source(1 to nrMessages)
      .throttle(messagesPerSecond, 1.second)
      .map(id => Measurement(id, Math.random() * 100, LocalDateTime.now))
      .map(meas => meas.toJson)

    val tapIn = Flow[String].wireTap(req => tracef(req, "--> %s", tracingEnabled))
    val tapOut = Flow[String].wireTap(res => tracef(res, "<-- %s", tracingEnabled))

    source.via(tapIn).via(service).via(tapOut).run()
  }
end ServerSimulator