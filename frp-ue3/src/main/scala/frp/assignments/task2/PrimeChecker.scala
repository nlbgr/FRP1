package frp.assignments.task2

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object PrimeChecker:
  sealed trait Command
  final case class CheckPrime(message: InputMessage, replyTo: ActorRef[OutputMessage]) extends Command
  private final case class FactorsComputed(n: Int, factors: Seq[Int], replyTo: ActorRef[OutputMessage]) extends Command
  private final case class FactorsComputedFailed(n: Int, replyTo: ActorRef[OutputMessage]) extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      new PrimeChecker(context)
    }

private class PrimeChecker(context: ActorContext[PrimeChecker.Command]) extends AbstractBehavior[PrimeChecker.Command](context):

  import PrimeChecker.{Command, CheckPrime, FactorsComputed, FactorsComputedFailed}

  override def onMessage(msg: Command): Behavior[Command] = msg match
    case CheckPrime(msg, replyTo) =>
      context.pipeToSelf(factor(msg.number)) {
        case Success(factors) => FactorsComputed(msg.number, factors, replyTo)
        case Failure(_) => FactorsComputedFailed(msg.number, replyTo)
      }
      Behaviors.same

    case FactorsComputed(n, factors, replyTo) =>
      replyTo ! OutputMessage(n, factors.size == 2 && factors.contains(1) && factors.contains(n), factors)
      Behaviors.same
    case FactorsComputedFailed(n, replyTo) =>
      replyTo ! OutputMessage(n, false, Seq.empty)
      Behaviors.same

  private def factor(n: Int): Future[Seq[Int]] = Future {
    if (n <= 1) Seq.empty
    else (1 to n).filter(n % _ == 0)
  }
