package dive.poolack.routes

import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import dive.poolack.api.IssueApi
import scala.concurrent.Future
import dive.poolack.routes.ResponseHandler
import scala.util.Success
import scala.util.Failure
import dive.poolack.routes.JsonSupport._
import dive.poolack.Issue

// I implemented 3 different ways of handling the responses of our api methods. There is a description
// for each method below. For more details please read comments in IssueApi and ResponseHandler files.
object MainRouter {
  private val apiRoute: Route = pathPrefix("api" / "issues") {
    path("all") {
      // * First Option: handle the state of each result seperately,
      //   in my opinion this will lead to a code duplication but we have more control over our responses
      get {
        val result = IssueApi.getAllIssues()
        onComplete(result) {
          _ match {
            case Success(value) => complete(value)
            case Failure(exception) =>
              complete(ResponseHandler.handleError(exception))
          }
        }
      }
    } ~ pathEnd {
      // * Second Option: we can use a global function that handles the errors and execptions
      // asumming that the result of our issueApi methods are infact the results that we want to
      // send to our users, this will be much shorter and more efficient
      delete {
        parameters("id") { id =>
          ResponseHandler.handleResponse(IssueApi.removeIssue(id))
        }
      }
    } ~
      // * Third Option: we can modify both of the above options and use Future[Either[BaseError, T]] instead of Future[T]
      // In my opinion this is the best implemntation among the three options.
      pathEnd {
        post {
          entity(as[Issue]) { body =>
            ResponseHandler.handleEitherResponse(IssueApi.addIssue(body))
          }
        }
      }
  }

  private val docRouter: Route =
    pathPrefix("docs") {
      get {
        getFromResourceDirectory("docs")
      }
    } ~
      path("swagger") {
        get {
          getFromResource("swagger.html")
        }
      }

  val route: Route =
    apiRoute ~ docRouter

}
