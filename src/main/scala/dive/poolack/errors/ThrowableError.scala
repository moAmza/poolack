package dive.poolack.errors

final case class ErrorResponse(status: Boolean, message: String)

trait ThrowableError extends Throwable {
  val message: String = "default message"

  def parseError() = {
    ErrorResponse(status = false, message = message)
  }
}

case class ThrowableSimpleError(override val message: String)
    extends ThrowableError
