package dive.poolack.errors

import dive.poolack.ErrorResponse

trait ThrowableError extends Throwable {
  val message: String = "default message"

  def parseError() = {
    ErrorResponse(status = false, message = message)
  }
}

case class ThrowableSimpleError(override val message: String)
    extends ThrowableError
