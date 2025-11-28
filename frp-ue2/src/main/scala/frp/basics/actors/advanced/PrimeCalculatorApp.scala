package frp.basics.actors.advanced

import org.slf4j.LoggerFactory

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors


import scala.concurrent.Await
import scala.concurrent.duration.Duration

@main
def primeCalculatorMain(): Unit =
  // Force SLF4J/Logback to initialize early so that any logging calls made during
  // Pekko (Akka) startup  are not buffered and replay warnings are avoided.
  LoggerFactory.getLogger("bootstrap")
  
  println("==================== PrimeCalculatorApp ==========================")

  val system = ActorSystem(MainActor(), "prime-calculator-system")

  Await.ready(system.whenTerminated, Duration.Inf)
end primeCalculatorMain
