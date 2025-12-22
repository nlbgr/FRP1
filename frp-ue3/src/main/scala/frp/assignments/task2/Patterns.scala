package frp.assignments.task2

//import frp.assignments.task2.PrimeCheckerClient1.Start
import frp.assignments.task2.PrimeCheckerClient2.Start
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors

object Patterns:
  @main
  def main(): Unit = {
    val system: ActorSystem[PrimeChecker.Command] = ActorSystem(
      Behaviors.setup { context =>
        val checker = context.spawn(PrimeChecker(), "checker")
        //val client1 = context.spawn(PrimeCheckerClient1(checker), "client1")
        val client2 = context.spawn(PrimeCheckerClient2(checker), "client2")

        //client1 ! Start
        client2 ! Start

        Behaviors.empty

      }, "system")
  }
