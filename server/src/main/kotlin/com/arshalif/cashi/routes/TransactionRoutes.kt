package com.arshalif.cashi.routes

import com.arshalif.cashi.service.TransactionApplicationService
import com.arshalif.cashi.core.remote.ApiResponse
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.transactionRoutes(
    transactionService: TransactionApplicationService
) {
    route("/transactions") {
        get {
            try {
                val transactions = transactionService.getAllTransactions()

                val response = ApiResponse(
                    success = true,
                    message = "transaction fetched successfully",
                    data = transactions
                )

                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                val errorResponse = ApiResponse<List<TransactionDto>>(
                    success = false,
                    message = "Internal server error",
                    error = e.message ?: "Unknown error"
                )
                call.respond(HttpStatusCode.InternalServerError, errorResponse)
            }
        }
    }
} 