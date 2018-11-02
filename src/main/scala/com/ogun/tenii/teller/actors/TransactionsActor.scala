package com.ogun.tenii.teller.actors

import java.time.LocalDate

import akka.actor.{Actor, ActorSystem}
import com.ogun.tenii.teller.external.HttpTransfers
import com.ogun.tenii.teller.model.{GetTransactionsRequest, TellerTransaction}
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class TransactionsActor extends Actor with LazyLogging with TellerEndpoints {

  implicit val system: ActorSystem = context.system
  val http = new HttpTransfers()
  implicit val timeout: FiniteDuration = 10.seconds

  override def receive: Receive = {
    case request: GetTransactionsRequest =>
      val senderRef = sender()
      http.endpointGet[List[TellerTransaction]](s"$apiHost$accounts${request.accountId}$transactions", ("Authorization", s"Bearer ${request.id}")).onComplete {
        case Success(resp) => senderRef ! resp
          val today = LocalDate.now()
          val month = today.getMonthValue
          val formattedMonth = if(month < 10) "0" + month else month.toString
          val date = Integer.parseInt(s"${today.getYear}$formattedMonth${today.getDayOfMonth}")
          val transactions = resp.takeWhile(i => Integer.parseInt(i.date.replace("-","")) >= date)
          if(transactions.nonEmpty) {
            //TODO send to service to process transactions
            //self ! (transactions, request.accountId)
            logger.info(s"Sending ${transactions.size} transactions for potential tenii payments")
          }
          else {
            logger.info(s"No transactions to process")
          }
      case Failure(t) => senderRef ! t
    }
    case other => logger.error(s"Unknown message received $other")
  }
}
