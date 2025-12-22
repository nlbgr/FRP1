package frp.assignments.task1

import org.apache.pekko.actor.typed.ActorRef

final case class Message (
  id: Long,
  text: String,
  replyTo: ActorRef[Ack]
)

final case class Ack(id: Long)

