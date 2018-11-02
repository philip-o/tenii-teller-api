package com.ogun.tenii.teller.model

case class GetAccountRequest(id: String)

case class BankAccount(userId: String, provider: String, sortCode: String, accountNumber: String, balance: BigDecimal)

case class GetAccountResponse(account: Option[BankAccount], cause: Option[String] = None)

case class TellerAccountResponse(name: String, links: Links, institution: String, id: String, enrollment_id: String, customer_type: String, currency: String, bank_code: String, balance: String, account_number: String)

case class Links(transactions: String)