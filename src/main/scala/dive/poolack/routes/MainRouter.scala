package dive.poolack.routes

import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import dive.poolack.Issue
import JsonSupport.issueFormat
import dive.poolack.api.IssueApi
import scala.util.Success
import scala.util.Try
import scala.util.Failure
import dive.poolack.BaseError
import dive.poolack.ServerError
import akka.compat.Future
import scala.concurrent.Future
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.RouteResult
import spray.json._
import akka.http.scaladsl.marshalling._

object MainRouter {
  private def handleResponse[T](
      response: => Future[T]
  ): RequestContext => Future[RouteResult] = {
    onComplete(response) {
      _ match {
        case Success(value) => complete(value)
        case Failure(exception) =>
          exception match {
            case error: BaseError => complete(error)
            case _                => complete(ServerError())
          }
      }
    }
  }
  private val apiRoute: Route = pathPrefix("api" / "issues") {
    pathEnd {
      post {
        entity(as[Issue]) { body =>
          handleResponse(IssueApi.addIssue(body))
        }
      }
    } ~ pathEnd {
      delete {
        parameters("id") { id =>
          handleResponse(IssueApi.removeIssue(id))
        }
      }
    } ~ pathEnd {
      get {
        complete("Hello")
      }
    } ~
      path("all") {
        get {
          handleResponse(IssueApi.getAllIssues())
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
