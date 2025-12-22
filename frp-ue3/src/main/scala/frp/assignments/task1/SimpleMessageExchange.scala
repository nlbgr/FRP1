package frp.assignments.task1

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors

object SimpleMessageExchange:
  @main
  def main(): Unit = {
    val system: ActorSystem[MessageSender.Command] = ActorSystem(
      Behaviors.setup { context =>
        val receiver = context.spawn(MessageReceiver(), "receiver")
        val sender = context.spawn(MessageSender(receiver, 1), "sender")

        sender ! MessageSender.SendMessages
        sender ! MessageSender.SendMessages
        sender ! MessageSender.SendMessages
        sender ! MessageSender.SendMessages
        sender ! MessageSender.SendMessages

        Behaviors.same
      }, "system")
  }
