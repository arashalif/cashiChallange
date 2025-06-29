package com.arshalif.cashi.presentation.routes

import com.arshalif.cashi.application.service.PaymentApplicationService
import com.arshalif.cashi.features.payment.data.model.PaymentRequestDto
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock

fun Route.paymentRoutes(
    paymentService: PaymentApplicationService,
    paymentValidator: PaymentValidator
) {
    route("/payments") {
        post {
            try {
                // Parse request
                val requestBody = call.receive<String>()
                val request = parsePaymentRequest(requestBody)
                
                // Create domain object for validation
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
                
                // Validate payment
                val validationResult = paymentValidator.validatePayment(payment)
                
                if (validationResult is com.arshalif.cashi.features.payment.domain.validation.ValidationResult.Invalid) {
                    call.respondText(
                        """{"success":false,"message":"Payment validation failed","error":"${validationResult.message}"}""",
                        status = HttpStatusCode.BadRequest,
                        contentType = ContentType.Application.Json
                    )
                    return@post
                }
                
                // Process payment
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
    }
}

// Simple JSON parser for demo purposes
private fun parsePaymentRequest(json: String): PaymentRequestDto {
    val emailMatch = Regex("\"recipientEmail\"\\s*:\\s*\"([^\"]+)\"").find(json)
    val amountMatch = Regex("\"amount\"\\s*:\\s*([0-9.]+)").find(json)
    val currencyMatch = Regex("\"currency\"\\s*:\\s*\"([^\"]+)\"").find(json)
    
    return PaymentRequestDto(
        recipientEmail = emailMatch?.groupValues?.get(1) ?: "",
        amount = amountMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0,
        currency = currencyMatch?.groupValues?.get(1) ?: ""
    )
} 