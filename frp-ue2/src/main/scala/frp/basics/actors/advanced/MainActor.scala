package frp.basics.actors.advanced

import scala.concurrent.duration.DurationInt
import org.apache.pekko.actor.typed.scaladsl.{ActorContext, Behaviors, PoolRouter, Routers}
import org.apache.pekko.actor.typed.{Behavior, SupervisorStrategy, Terminated}

object MainActor:

  def apply() =
    Behaviors.setup { context =>

      Behaviors.empty
    }
  end apply

end MainActor
