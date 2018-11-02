package com.ogun.tenii.teller.routes

import akka.http.scaladsl.model.Uri.Path.Segment
import akka.http.scaladsl.server.{Directive1, Directives, PathMatcher1}

trait RequestDirectives extends Directives {

  val userIdDirective: PathMatcher1[String] = Segment

  val transactionIdDirective: Directive1[String] = parameter("transactionsId")

}
