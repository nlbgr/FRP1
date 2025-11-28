package frp.basics.actors.advanced

import scala.concurrent.duration.DurationInt
import org.apache.pekko.actor.typed.scaladsl.{ActorContext, Behaviors, PoolRouter, Routers}
import org.apache.pekko.actor.typed.{Behavior, SupervisorStrategy, Terminated}

object MainActor:

  import PrimeCalculator._

  private def primeFinderPoolBehavior: Behavior[PrimeFinder.Command] = {
    Routers.pool(poolSize = 4) {
      Behaviors
        .supervise(PrimeFinder())
        .onFailure[ArithmeticException](SupervisorStrategy.restart)
    }
  }

  def apply(): Behavior[Reply] = {
    Behaviors.setup { context =>
      val workerPool = context.spawn(primeFinderPoolBehavior, "prime-finder-pool")
      context.spawn(PrimeCalculator(2, 100, context.self, workerPool), "primecalculator1")
      context.spawn(PrimeCalculator(1000, 2000, context.self, workerPool), "primecalculator2")
      context.spawn(PrimeCalculator(2, 1_000_000, context.self, workerPool, 100.millis), "primecalculator3")

      context.children.foreach(context.watch)

      Behaviors.receiveMessage[Reply] {
        case Result(lower, upper, primes) =>
          println(s"primes in [$lower, $upper] = {${primes.mkString(",")}}")
          Behaviors.same
        case Failure(lower, upper, reason) =>
          println(s"FAILURE in [$lower, $upper]: $reason")
          Behaviors.same
      }.receiveSignal {
        case (context, Terminated(_)) =>
          if (context.children.size <= 1) {
            Behaviors.stopped
          } else {
            Behaviors.same
          }
      }
    }
  }


end MainActor
