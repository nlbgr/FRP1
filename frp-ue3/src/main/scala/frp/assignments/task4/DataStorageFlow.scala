package frp.assignments.task4

import org.apache.pekko.{Done, NotUsed}
import org.apache.pekko.stream.scaladsl.{FileIO, Flow, Keep, Sink}
import org.apache.pekko.util.ByteString

import java.nio.file.Paths
import scala.concurrent.duration.*

object DataStorageFlow:
  def apply(
    filePath: String,
  ): Flow[Measurement, Measurement, ?] =
    val fileSink =
      Flow[Measurement]
        .map(m => ByteString(s"${m.timestamp}, ${m.measurement},  ${Thread.currentThread.threadId}\n"))
        .to(FileIO.toPath(Paths.get(filePath)))

    Flow[Measurement]
      .throttle(100, 1.second)
      .alsoTo(fileSink)