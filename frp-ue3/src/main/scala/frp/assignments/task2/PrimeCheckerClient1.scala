package frp.assignments.task2

import frp.assignments.task2.PrimeChecker
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors

object PrimeCheckerClient1:
  sealed trait Command
  case object Start extends Command
  private final case class WrappedOutMessage(msg: OutputMessage) extends Command

  def apply(checker: ActorRef[PrimeChecker.CheckPrime]): Behavior[Command] =
    Behaviors.setup {context =>
      Behaviors.receiveMessage {
        case Start =>
          (2 to 1000).foreach(n =>
            checker ! PrimeChecker.CheckPrime(InputMessage(n), context.messageAdapter(WrappedOutMessage))
          )

          Behaviors.same

        case WrappedOutMessage(msg) =>
          if msg.isPrime then
            println(s"Received Answer! ${msg.number} is prime and has factors: ${msg.factors}")
          else
            println(s"Received Answer! ${msg.number} is not prime and has factors: ${msg.factors}")
          Behaviors.same
      }
    }
