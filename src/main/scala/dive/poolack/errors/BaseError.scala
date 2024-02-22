package dive.poolack.errors

import dive.poolack.ErrorResponse

trait BaseError {
  val message: String = "default message"

  def parseError() = {
    ErrorResponse(status = false, message = message)
  }
}

case class SimpleError(override val message: String) extends BaseError
case class ServerError(e: Throwable) extends BaseError {
  override val message: String = "server error"

  println("SERVER ERROR: ", e) // log server error for further investigation
}
