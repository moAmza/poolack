package dive.poolack.api

import dive.poolack.Issue
import dive.poolack.persist.IssueRepo
import akka.compat.Future
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import dive.poolack.errors.SimpleError
import dive.poolack.errors.ThrowableSimpleError
object IssueApi {
  def getAllIssues()(implicit ec: ExecutionContext): Future[List[Issue]] = {
    IssueRepo.getAllIssues()
  }

  // * Api implementation for Option 2: Injecting BaseError(Our logic errors) into Future using flatmap
  //   since the input of Future.failed() should be Throwable we declared ThrowableSimpleError that extends Throwable interface
  def removeIssue(
      issueId: String
  )(implicit ec: ExecutionContext): Future[Issue] = {
    IssueRepo
      .removeIssue(issueId)
      .flatMap(option =>
        option match {
          case Some(issue) => Future.successful(issue)
          case None => Future.failed(ThrowableSimpleError("Issue not found"))
        }
      )
  }

  // * Api implementation for Option 3:  Simply use Future[Either[T]]
  //   here we don't have the Throwable constraint that we had in Option 2 and thus we use SimpleError instead of ThrowableSimpleError (which does not extends Throwable)
  def addIssue(
      issue: Issue
  )(implicit ec: ExecutionContext): Future[Either[SimpleError, String]] = {
    // Some business logic ...
    if (issue.id.length() > 10) {
      Future.successful(Left(new SimpleError("Issue id too long")))
    } else
      IssueRepo.insertIssue(issue).map(Right(_))
  }

  // * Api implementation for Option 4:  Simply use Future[Either[T]]
  def getIssue(
      issueId: String
  )(implicit ec: ExecutionContext): Future[Either[SimpleError, Issue]] = {
    IssueRepo
      .getIssue(issueId)
      .map(_ match {
        case Some(issue) => Right(issue)
        case None        => Left(new SimpleError("Issue not found"))
      })
  }
}
