package com.ogun.tenii.teller.routes

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import akka.pattern.{CircuitBreaker, ask}
import akka.util.Timeout
import com.ogun.tenii.teller.actors.TransactionsActor
import com.ogun.tenii.teller.model.{GetTransactionsRequest, GetTransactionsResponse, TellerTransaction}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import javax.ws.rs.Path

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

@Path("transactions")
class TransactionsRoute(implicit system: ActorSystem, breaker: CircuitBreaker) extends RequestDirectives with LazyLogging {

  implicit val executor: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)
  protected val transactionsActor: ActorRef = system.actorOf(Props[TransactionsActor])

  def route: Route = pathPrefix("transactions") {
    getTransactions
  }

  def getTransactions: Route =
    get {
      path(userIdDirective / userIdDirective).as(GetTransactionsRequest) { request =>
        logger.info(s"POST /transaction - $request")
        onCompleteWithBreaker(breaker)(transactionsActor ? request) {
          case Success(msg: List[TellerTransaction]) => complete(StatusCodes.OK -> GetTransactionsResponse(msg))
          case Failure(t) => failWith(t)
        }
      }
    }

}
