package frp.basics.actors.advanced

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior, PostStop, PreRestart}
import frp.basics.actors.PrimeUtil.isPrime

import scala.util.Random

object PrimeFinder:

  sealed trait Command
  final case class Find(lower: Int, upper: Int, replyTo: ActorRef[Reply]) extends Command

  sealed trait Reply
  final case class PartialResult(lower: Int, upper: Int, primes: Seq[Int]) extends Reply

  private def failSometimes(probability: Double, lower: Int, upper: Int, actorName: String): Unit = {
    if (math.random() <= probability)
      throw new ArithmeticException(s"  $actorName (Thread ${Thread.currentThread.threadId}) [$lower, $upper] FAILED)")
  }

  def apply(): Behavior[Command] =
    Behaviors
      .receive[Command] {
        case (context, Find(lower, upper, replyTo)) =>
          println(s"  ${context.self.path.name} (Thread ${Thread.currentThread.threadId}) [$lower, $upper] STARTED")
          val primes = (lower to upper) filter isPrime
          failSometimes(0.4, lower, upper, context.self.path.name)

          println(s"  ${context.self.path.name} (Thread ${Thread.currentThread.threadId}) [$lower, $upper] SUCCEEDED -> {${primes.mkString(",")}}")


          replyTo ! PartialResult(lower, upper, primes)
          Behaviors.same
      }.receiveSignal {
        case (context, PostStop) =>
          println(s"  ${context.self.path.name} (Thread ${Thread.currentThread.threadId}) STOPPED")
          Behaviors.same
        case (context, preRestart) =>
          println(s"  ${context.self.path.name} (Thread ${Thread.currentThread.threadId}) RESTARTED")
          Behaviors.same
      }
  end apply
end PrimeFinder