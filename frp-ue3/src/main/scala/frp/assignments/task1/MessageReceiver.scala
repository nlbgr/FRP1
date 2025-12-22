package frp.assignments.task1

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import scala.util.Random

object MessageReceiver:
  def apply(): Behavior[Message] =
    Behaviors.receive { (context, message) =>
      println(s"Received Message: $message")

      // lost acks
      if Random.nextFloat() < 0.5 then
        message.replyTo ! Ack(message.id)

      Behaviors.same
    }
