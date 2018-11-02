package com.ogun.tenii.teller.actors

trait TellerEndpoints {

  val apiHost = "https://api.teller.io/"
  val appId = "application_id="
  val accounts = "accounts/"
  val transactions = "/transactions"

  implicit def onSuccessDecodingError[TellerTeniiPaymentsResponse](decodingError: io.circe.Error): TellerTeniiPaymentsResponse = throw new Exception(s"Error decoding trains upstream response: $decodingError")
  implicit def onErrorDecodingError[TellerTeniiPaymentsResponse](decodingError: String): TellerTeniiPaymentsResponse = throw new Exception(s"Error decoding upstream error response: $decodingError")

}