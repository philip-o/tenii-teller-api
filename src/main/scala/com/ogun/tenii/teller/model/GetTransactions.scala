package com.ogun.tenii.teller.model

case class GetTransactionsRequest(id: String, accountId: String)

case class TellerTransaction(running_balance: String, description: String, date: String, id: String, counterparty: String, amount: String)

case class GetTransactionsResponse(transactions: List[TellerTransaction])
