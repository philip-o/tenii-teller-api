package com.ogun.tenii.teller.routes

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.{CircuitBreaker, ask}
import akka.util.Timeout
import com.ogun.tenii.teller.actors.AccountsActor
import com.ogun.tenii.teller.model.{GetAccountRequest, GetAccountResponse, TellerAccountResponse}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import javax.ws.rs.Path

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

@Path("accounts")
class AccountsRoute(implicit system: ActorSystem, breaker: CircuitBreaker) extends RequestDirectives with LazyLogging {

  implicit val executor: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)
  protected val accountsActor: ActorRef = system.actorOf(Props[AccountsActor])

  def route: Route = pathPrefix("accounts") {
    getAccounts
  }

  def getAccounts: Route =
    get {
      path(userIdDirective).as(GetAccountRequest) { request =>
        logger.info(s"POST /accounts - $request")
        onCompleteWithBreaker(breaker)(accountsActor ? request) {
          case Success(msg: List[TellerAccountResponse]) => complete(StatusCodes.OK -> msg)
          case Success(other: Exception) => complete(StatusCodes.InternalServerError -> other)
          case Failure(t) => failWith(t)
        }
      }
    }
}
