package com.ogun.tenii.teller.actors

import akka.actor.{Actor, ActorSystem}
import com.ogun.tenii.teller.external.HttpTransfers
import com.ogun.tenii.teller.model.{BankAccount, GetAccountRequest, GetAccountResponse, TellerAccountResponse}
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class AccountsActor extends Actor with LazyLogging with TellerEndpoints {

  implicit val system: ActorSystem = context.system
  val http = new HttpTransfers()
  implicit val timeout: FiniteDuration = 10.seconds

  override def receive: Receive = {
    case request: GetAccountRequest =>
      //TODO logic to create tenii pot for new user
      //Send teller Id and limit
      val senderRef = sender()
      http.endpointGet[List[TellerAccountResponse]](s"$apiHost$accounts", ("Authorization", s"Bearer ${request.id}")).onComplete {
        case Success(resp) => senderRef ! resp
        case Failure(t) => senderRef ! t
      }

    case other => logger.error(s"Unknown message received $other")
  }
}
