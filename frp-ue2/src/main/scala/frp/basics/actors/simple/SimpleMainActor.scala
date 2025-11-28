package frp.basics.actors.simple

import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import org.apache.pekko.actor.typed.{ActorRef, Behavior, Signal, Terminated}

object SimpleMainActor:
  def behavior: Behavior[SimplePrimeCalculator.Reply] = Behaviors.setup(context => new SimpleMainActor(context))
end SimpleMainActor

private class SimpleMainActor(context: ActorContext[SimplePrimeCalculator.Reply])
  extends AbstractBehavior(context):

  import SimplePrimeCalculator._

  //var calc: ActorRef[Command] = context.spawn(SimplePrimeCalculator.ooBehavior, "prime-calculator")
  var calc: ActorRef[Command] = context.spawn(SimplePrimeCalculator.functionalBehavior, "prime-calculator")
  calc ! Find(2, 100, context.self)
  calc ! Find(1000, 2000, context.self)
  calc ! Shutdown

  context.watch(calc)

  override def onMessage(msg: Reply): Behavior[Reply] = {
    msg match
      case Found(lower, upper, primes) =>
        println(s"primes in [$lower, $upper] = {${primes.mkString(",")}}")
        Behaviors.same
  }

  override def onSignal = {
    case Terminated(_) => Behaviors.stopped
  }
end SimpleMainActor