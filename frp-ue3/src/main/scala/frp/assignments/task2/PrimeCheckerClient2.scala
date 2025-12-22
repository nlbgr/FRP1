package frp.assignments.task2

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior, Scheduler}
import org.apache.pekko.pattern.AskTimeoutException
import org.apache.pekko.actor.typed.scaladsl.AskPattern.*
import org.apache.pekko.util.Timeout

import scala.concurrent.duration.*
import scala.util.Failure
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PrimeCheckerClient2:
  sealed trait Command
  case object Start extends Command

  def apply(checker: ActorRef[PrimeChecker.CheckPrime]): Behavior[Command] =
    Behaviors.setup {context =>
      given Scheduler = context.system.scheduler
      given Timeout = 1.second //1.milli

      Behaviors.receiveMessage {
        case Start =>
          (2 to 1000).foreach(n =>
            val res: Future[OutputMessage] = checker ? ((replyTo: ActorRef[OutputMessage]) => PrimeChecker.CheckPrime(InputMessage(n), replyTo))

            res.onComplete {
              case util.Success(msg) =>
                if msg.isPrime then
                  println(s"Received Answer! ${msg.number} is prime and has factors: ${msg.factors}")
                else
                  println(s"Received Answer! ${msg.number} is not prime and has factors: ${msg.factors}")

              case Failure(_: AskTimeoutException) =>
                println(s"computation for $n took to long")

              case Failure(ex) =>
                println(ex.getMessage)
            }
          )

          Behaviors.same
      }
    }
