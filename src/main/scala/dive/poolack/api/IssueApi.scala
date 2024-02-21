package dive.poolack.api

import dive.poolack.BaseError
import dive.poolack.Issue
import dive.poolack.persist.IssueRepo
import akka.compat.Future
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

object IssueApi {

  def addIssue(issue: Issue)(implicit ec: ExecutionContext): Future[Int] = {
    IssueRepo.insertIssue(issue)
  }

  def removeIssue(
      issueId: String
  )(implicit ec: ExecutionContext): Future[Issue] = {
    IssueRepo
      .removeIssue(issueId)
      .flatMap(_ match {
        case Some(issue) => Future.successful(issue)
        case None        => Future.failed(BaseError("Issue not found"))
      })
  }

  def getAllIssues()(implicit ec: ExecutionContext): Future[List[Issue]] = {
    IssueRepo.getAllIssues()
  }
}
