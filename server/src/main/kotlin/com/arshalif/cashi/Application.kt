package com.arshalif.cashi

import com.arshalif.cashi.features.payment.data.model.PaymentRequestDto
import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.model.Currency
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val recipientEmail: String,
    val amount: Double,
    val currency: String
)

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val paymentValidator = PaymentValidator()
    val paymentService = PaymentService()
    
    routing {
        get("/") {
            call.respondText("Cashi Payment API Server is running!")
        }
        
        // POST /payments endpoint as required by the assignment
        post("/payments") {
            try {
                // Use manual JSON parsing for now
                val requestBody = call.receive<String>()
                val request = parsePaymentRequest(requestBody)
                
                // Create a Payment domain object for validation
                val payment = Payment(
                    recipientEmail = request.recipientEmail,
                    amount = request.amount,
                    currency = try {
                        Currency.valueOf(request.currency.uppercase())
                    } catch (e: IllegalArgumentException) {
                        call.respondText(
                            """{"success":false,"message":"Payment validation failed","error":"Unsupported currency: ${request.currency}"}""",
                            status = HttpStatusCode.BadRequest,
                            contentType = ContentType.Application.Json
                        )
                        return@post
                    },
                    timestamp = Clock.System.now()
                )
                
                // Validate the payment request
                val validationResult = paymentValidator.validatePayment(payment)
                
                if (validationResult is com.arshalif.cashi.features.payment.domain.validation.ValidationResult.Invalid) {
                    call.respondText(
                        """{"success":false,"message":"Payment validation failed","error":"${validationResult.message}"}""",
                        status = HttpStatusCode.BadRequest,
                        contentType = ContentType.Application.Json
                    )
                    return@post
                }
                
                // Process the payment
                val processedPayment = paymentService.processPayment(
                    recipientEmail = request.recipientEmail,
                    amount = request.amount,
                    currency = request.currency
                )
                
                call.respondText(
                    """{"success":true,"message":"Payment processed successfully","payment":{"id":"${processedPayment.id}","recipientEmail":"${processedPayment.recipientEmail}","amount":${processedPayment.amount},"currency":"${processedPayment.currency}","timestamp":"${processedPayment.timestamp}"}}""",
                    status = HttpStatusCode.Created,
                    contentType = ContentType.Application.Json
                )
                
            } catch (e: Exception) {
                call.respondText(
                    """{"success":false,"message":"Internal server error","error":"${e.message ?: "Unknown error"}"}""",
                    status = HttpStatusCode.InternalServerError,
                    contentType = ContentType.Application.Json
                )
            }
        }
        
        // GET /transactions endpoint to retrieve transaction history
        get("/transactions") {
            try {
                val transactions = paymentService.getTransactions()
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
        
        // Health check endpoint
        get("/health") {
            call.respondText(
                """{"status":"healthy","timestamp":"${Clock.System.now()}"}""",
                status = HttpStatusCode.OK,
                contentType = ContentType.Application.Json
            )
        }
    }
}

// Simple JSON parser for demo purposes
private fun parsePaymentRequest(json: String): PaymentRequest {
    // This is a very basic JSON parser for demo
    // In production, you'd use proper JSON serialization
    val emailMatch = Regex("\"recipientEmail\"\\s*:\\s*\"([^\"]+)\"").find(json)
    val amountMatch = Regex("\"amount\"\\s*:\\s*([0-9.]+)").find(json)
    val currencyMatch = Regex("\"currency\"\\s*:\\s*\"([^\"]+)\"").find(json)
    
    return PaymentRequest(
        recipientEmail = emailMatch?.groupValues?.get(1) ?: "",
        amount = amountMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0,
        currency = currencyMatch?.groupValues?.get(1) ?: ""
    )
}