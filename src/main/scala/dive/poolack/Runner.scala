package dive.poolack

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import dive.poolack.routes.MainRouter
import akka.compat.Future
import scala.concurrent.Future
import scala.util.Left

object Runner {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("poolack-http-system")
    val bindingFuture =
      Http()
        .newServerAt("0.0.0.0", 8080)
        .bind(MainRouter.route)
    println("Server Running")
  }
}
