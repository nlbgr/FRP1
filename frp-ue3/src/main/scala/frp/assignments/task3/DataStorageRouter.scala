package frp.assignments.task3

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{Behaviors, Routers}

object DataStorageRouter:

  def apply(numWorkers: Int): Behavior[Measurement] =
    Behaviors.setup { context =>
      var workerIndex = 0

      val router = context.spawn(
        Routers.pool(poolSize = numWorkers) {
          var counter = 0
          Behaviors.setup[Measurement] { _ =>
            counter += 1
            val filePath = s"test${counter}.csv"
            DataStorageStreamIo(filePath)
          }
        }, "dataStoreRouter"
      )

      Behaviors.receiveMessage { measurement =>
        router ! measurement
        Behaviors.same
      }
    }
