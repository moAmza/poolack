package dive.poolack.persist

import dive.poolack.Issue
import scala.concurrent.Future
import scala.collection.mutable

object IssueRepo {

  private val data: mutable.HashMap[String, Issue] =
    mutable.HashMap[String, Issue]()

  def insertIssue(issue: Issue): Future[Int] = {
    this.synchronized {
      data.put(issue.id, issue)
    }

    data.foreach(println)

    Future.successful(data.size)
  }

  def removeIssue(id: String): Future[Option[Issue]] = {
    val issue = this.synchronized {
      data.remove(id)
    }

    Future.successful(issue)
  }

  def getAllIssues(): Future[List[Issue]] = {
    Future.successful(data.toList.map(_._2))
  }
}
