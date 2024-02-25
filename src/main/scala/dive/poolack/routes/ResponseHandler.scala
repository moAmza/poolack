package dive.poolack.routes

import dive.poolack.errors._
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.server.RouteResult
import scala.concurrent.Future
import akka.http.scaladsl.server.Directives._
import scala.util.Success
import scala.util.Failure
import JsonSupport.errorFormat
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import dive.poolack.ErrorResponse

object ResponseHandler {
  // This handler is used in option 1 and 2
  def handleError(exception: Throwable): ErrorResponse = {
    exception match {
      case error: ThrowableError => error.parseError()
      case e                     => ServerError(e).parseError()
    }
  }

  // This handler is used in option 2
  def handleResponse[T](
      response: => Future[T]
  )(implicit
      marshaller: ToResponseMarshaller[T]
  ): RequestContext => scala.concurrent.Future[RouteResult] = {
    onComplete(response) {
      _ match {
        case Success(value) => complete(value)
        case Failure(e)     => complete(handleError(e))
      }
    }
  }

  // This handler is used in option 3
  def handleEitherResponse[T](
      response: => Future[Either[BaseError, T]]
  )(implicit
      marshaller: ToResponseMarshaller[T]
  ): RequestContext => Future[RouteResult] = {
    onComplete(response) {
      _ match {
        case Success(value) =>
          value match {
            case Right(value) => complete(value)
            case Left(error)  => complete(error.parseError())
          }
        case Failure(e) => complete(ServerError(e).parseError())
      }
    }
  }

  // This handler is used in option 4
  def handleEither[T](value: Either[BaseError, T])(implicit
      marshaller: ToResponseMarshaller[T]
  ) = {
    value match {
      case Right(value) => complete(value)
      case Left(error)  => complete(error.parseError())
    }
  }
}
