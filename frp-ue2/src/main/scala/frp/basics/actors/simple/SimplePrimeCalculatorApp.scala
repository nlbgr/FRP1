package frp.basics.actors.simple

import org.slf4j.LoggerFactory

import org.apache.pekko.actor.typed.ActorSystem
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@main
def simplePrimeCalculatorMain(): Unit = {
  // Force SLF4J/Logback to initialize early so that any logging calls made during
  // Pekko (Akka) startup  are not buffered and replay warnings are avoided.
  LoggerFactory.getLogger("bootstrap")

  println("==================== SimplePrimeCalculatorApp ==========================")

  val system = ActorSystem(SimpleMainActor.behavior, "simple-prime-calculator-system")
  Await.ready(system.whenTerminated, Duration.Inf)
}

