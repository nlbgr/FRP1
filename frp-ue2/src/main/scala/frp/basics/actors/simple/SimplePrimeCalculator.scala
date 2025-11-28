package frp.basics.actors.simple

import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import frp.basics.actors.PrimeUtil.isPrime

object SimplePrimeCalculator:
  sealed trait Command
  final case class Find(lower: Int, upper: Int, replyTo: ActorRef[Reply]) extends Command
  case object Shutdown extends Command

  sealed trait Reply
  final case class Found(lower: Int, upper: Int, primes: Seq[Int]) extends Reply

  def ooBehavior: Behavior[Command] = Behaviors.setup(context => new SimplePrimeCalculator(context))

  def functionalBehavior: Behavior[Command] =
    Behaviors.receiveMessage {
      case Find(lower, upper, replyTo) =>
        val primes = (lower to upper).filter(isPrime)
        //replyTo.tell(Found(lower, upper, primes))
        replyTo ! Found(lower, upper, primes) // same as above because ! is the "bang" operator which delegates to tell
        Behaviors.same
      case Shutdown => Behaviors.stopped
    }

end SimplePrimeCalculator

class SimplePrimeCalculator(context: ActorContext[SimplePrimeCalculator.Command])
  extends AbstractBehavior(context):

  import SimplePrimeCalculator._

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match
      case Find(lower, upper, replyTo) =>
        val primes = (lower to upper).filter(isPrime)
        //replyTo.tell(Found(lower, upper, primes))
        replyTo ! Found(lower, upper, primes) // same as above because ! is the "bang" operator which delegates to tell
        Behaviors.same
      case Shutdown => Behaviors.stopped
  }
end SimplePrimeCalculator
