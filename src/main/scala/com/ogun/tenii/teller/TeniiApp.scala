package com.ogun.tenii.teller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.pattern.CircuitBreaker
import akka.stream.ActorMaterializer
import com.ogun.tenii.teller.routes.{AccountsRoute, TransactionsRoute}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.util.Properties

object TeniiApp extends App with LazyLogging with RouteConcatenation {

  val applicationName = "tenii-teller"

  implicit val system = ActorSystem(applicationName)
  implicit val mat = ActorMaterializer()

  logger.info(s"Number of processors visible to $applicationName service: ${Runtime.getRuntime.availableProcessors()}")

  implicit val breaker = CircuitBreaker(system.scheduler, 20, 100 seconds, 12 seconds)

  // routes
  //val swaggerDocRoute = new SwaggerDocRoute().routes
  val accountsRoute = new AccountsRoute().route
//  val bankAccountRoute = new BankAccountRoute().route
  val transactionRoute = new TransactionsRoute().route
//  val tellerRoute = new TellerRoute().route

  val routes = accountsRoute~ transactionRoute //~ tellerRoute //~ registerRoute ~ verifyRoute //~ searchRoute //buildInfoRoute.route ~ healthRoute ~ swaggerDocRoute ~ swaggerSiteRoute ~ prebookingRoute ~ vendorRoute

  val port = Properties.envOrElse("PORT", "8080").toInt
  Http().bindAndHandle(routes, "0.0.0.0", port)
  logger.info(s"$applicationName application started on port $port")

}
