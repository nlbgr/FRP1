package frp.basics.actors.advanced

import frp.basics.actors.RangeUtil
import org.apache.pekko.actor.typed.receptionist.Receptionist.Find

import scala.concurrent.duration.DurationInt
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors, TimerScheduler}
import org.apache.pekko.actor.typed.{ActorRef, Behavior}

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

object PrimeCalculator:
  sealed trait Command
  final case class WrappedPrimeFinderReply(reply: PrimeFinder.Reply) extends Command
  case object Resend extends Command
  case object Timeout extends Command

  sealed trait Reply
  final case class Result(lower: Int, upper: Int, primes: Seq[Int]) extends Reply
  final case class Failure(lower: Int, upper: Int, reason: String) extends Reply

  def apply(
             lower: Int,
             upper: Int,
             replyTo: ActorRef[Reply],
             workerPool: ActorRef[PrimeFinder.Command],
             maxDuration: FiniteDuration = 1.second
           ): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.withTimers(timer => new PrimeCalculator(context, lower, upper, replyTo, workerPool, timer, maxDuration))
    }
  end apply
end PrimeCalculator

private class PrimeCalculator(
                               context: ActorContext[PrimeCalculator.Command],
                               lower: Int,
                               upper: Int,
                               replyTo: ActorRef[PrimeCalculator.Reply],
                               workerPool: ActorRef[PrimeFinder.Command],
                               timer: TimerScheduler[PrimeCalculator.Command],
                               maxDuration: FiniteDuration
                             )
  extends AbstractBehavior[PrimeCalculator.Command](context):

  import PrimeCalculator._

  private val NR_WORKERS: Int = Runtime.getRuntime.availableProcessors
  private val RESEND_INTERVAL: FiniteDuration = 50.millis

  private val primes: mutable.Set[Int] = mutable.SortedSet.empty
  private val uncompletedTasks: mutable.Set[(Int, Int)] = mutable.Set.empty
  private val replyMapper: ActorRef[PrimeFinder.Reply] = context.messageAdapter(WrappedPrimeFinderReply(_))

  // impl of primary ctor
  createTasks()
  sendUncompletedTasks()
  timer.startTimerAtFixedRate(Resend, RESEND_INTERVAL)
  timer.startSingleTimer(Timeout, maxDuration)

  private def createTasks(): Unit = {
    uncompletedTasks ++= RangeUtil.splitIntoIntervals(lower, upper, NR_WORKERS)
  }

  private def sendUncompletedTasks(): Unit = {
    for ((lower, upper) <- uncompletedTasks) {
      workerPool ! PrimeFinder.Find(lower, upper, replyMapper)
    }
  }

  override def onMessage(msg: Command): Behavior[Command] =
    msg match
      case WrappedPrimeFinderReply(PrimeFinder.PartialResult(l, u, p)) =>
        primes ++= p
        uncompletedTasks -= ((l, u))
        if uncompletedTasks.isEmpty then
          replyTo ! Result(lower, upper, primes.toSeq)
          Behaviors.stopped
        else
          Behaviors.same
      case Resend =>
        sendUncompletedTasks()
        Behaviors.same
      case Timeout =>
        replyTo ! Failure(lower, upper, "Prime computation timed out")
        Behaviors.stopped

end PrimeCalculator
