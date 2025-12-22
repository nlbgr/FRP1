package frp.assignments.task1

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import scala.concurrent.duration._
import scala.util.Random

object MessageSender:
  sealed trait Command
  case object SendMessages extends Command
  final case class SendAck(ack: Ack) extends Command
  final case class Retry(id: Long) extends Command

  def apply(receiver: ActorRef[Message], maxRetries: Int): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.withTimers{ timers =>

        def running(
          pending: Map[Long, Message],
          retryCnt: Map[Long, Int]
        ): Behavior[Command] =
          Behaviors.receiveMessage {
            case SendMessages =>
              val id = Random.nextLong()
              val message1 = Message(id, "Test0", context.messageAdapter(SendAck))

              // simulate message lost
              if Random.nextFloat() < 0.5 then
                receiver ! message1

              timers.startSingleTimer(
                Retry(id),
                1.second
              )

              running(pending + (id -> message1), retryCnt + (id -> 0))
            case SendAck(ack) =>
              println(s"ack{$ack}")

              timers.cancel(Retry(ack.id))
              running(pending - ack.id, retryCnt - ack.id)

              Behaviors.same

            case Retry(id) =>
              pending.get(id) match
                case Some(message) =>

                  val numRetries = retryCnt.getOrElse(id, 0) + 1

                  if numRetries > maxRetries then
                    println(s"$id exceeded retrylimit of $maxRetries")

                    running(pending, retryCnt - id)
                  else

                    receiver ! message

                    timers.startSingleTimer(
                      Retry(id),
                      1.second
                    )

                    running(pending, retryCnt.updated(id, numRetries))
                case None => Behaviors.same
          }

        running(Map.empty, Map.empty)
      }
    }
