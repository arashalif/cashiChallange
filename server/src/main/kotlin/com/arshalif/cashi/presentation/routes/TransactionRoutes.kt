package com.arshalif.cashi.presentation.routes

import com.arshalif.cashi.application.service.TransactionApplicationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.transactionRoutes(
    transactionService: TransactionApplicationService
) {
    route("/transactions") {
        get {
            try {
                val transactions = transactionService.getAllTransactions()
                val transactionsJson = transactions.joinToString(",") { transaction ->
                    """{"id":"${transaction.id}","recipientEmail":"${transaction.recipientEmail}","amount":${transaction.amount},"currency":"${transaction.currency}","timestamp":"${transaction.timestamp}"}"""
                }
                call.respondText(
                    """{"success":true,"transactions":[$transactionsJson]}""",
                    status = HttpStatusCode.OK,
                    contentType = ContentType.Application.Json
                )
            } catch (e: Exception) {
                call.respondText(
                    """{"success":false,"transactions":[],"error":"${e.message ?: "Unknown error"}"}""",
                    status = HttpStatusCode.InternalServerError,
                    contentType = ContentType.Application.Json
                )
            }
        }
    }
} 