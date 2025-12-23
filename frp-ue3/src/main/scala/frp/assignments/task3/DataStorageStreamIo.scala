package frp.assignments.task3

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.javadsl.Behaviors
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.{FileIO, Source}
import org.apache.pekko.util.ByteString

import java.nio.file.Paths
import scala.concurrent.duration.*

object DataStorageStreamIo:

  def apply(filePath: String): Behavior[Measurement] =
    Behaviors.setup { context =>
      given Materializer = Materializer(context)

      val queue = Source
        .queue[ByteString](bufferSize = 128)
        .throttle(1000, 1.second)
        .to(
          FileIO.toPath(
            Paths.get(filePath)
          )
        )
        .run()

      Behaviors.receiveMessage { message =>
        println(s"store: {${message.timestamp}, ${message.measurement}}")

        queue.offer(ByteString(s"${message.timestamp}, ${message.measurement}\n"))

        Behaviors.same
      }
    }
