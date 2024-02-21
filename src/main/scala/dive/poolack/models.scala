package dive.poolack

final case class Issue(id: String, text: String)

trait CustomError extends Throwable
case class BaseError(message: String) extends CustomError
case class ServerError(message: String = "Internal server error")
    extends CustomError
