package dive.poolack.routes

import spray.json.DefaultJsonProtocol._
import spray.json._
import dive.poolack.Issue
import dive.poolack.ErrorResponse

object JsonSupport {

  implicit val issueFormat: RootJsonFormat[Issue] = jsonFormat2(Issue)
  implicit val errorFormat: RootJsonFormat[ErrorResponse] = jsonFormat2(
    ErrorResponse
  )

}
