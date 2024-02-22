package dive.poolack.persist

import dive.poolack.Issue
import scala.concurrent.Future
import scala.collection.mutable
import scala.concurrent.ExecutionContext

object IssueRepo {

  private val data: mutable.HashMap[String, Issue] =
    mutable.HashMap[String, Issue]()

  def insertIssue(issue: Issue): Future[String] = {
    this.synchronized {
      data.put(issue.id, issue)
    }

    Future.successful(data.size.toString())
  }

  def removeIssue(
      id: String
  )(implicit ec: ExecutionContext): Future[Option[Issue]] = {
    try {
      this.synchronized {
        Future.successful(data.remove(id))
      }
    } catch {
      case _: Throwable => Future.successful(None)
    }
  }

  def getIssue(id: String): Future[Option[Issue]] = {
    Future.successful(data.get(id))

  }

  def getAllIssues(): Future[List[Issue]] = {
    Future.successful(data.toList.map(_._2))
  }
}
