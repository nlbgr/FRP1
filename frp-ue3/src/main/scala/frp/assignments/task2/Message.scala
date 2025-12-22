package frp.assignments.task2

import org.apache.pekko.actor.typed.ActorRef

final case class InputMessage(number: Int)

final case class OutputMessage(number: Int, isPrime: Boolean, factors: Seq[Int])
