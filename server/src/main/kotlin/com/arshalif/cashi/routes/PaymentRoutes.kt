package com.arshalif.cashi.routes

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.service.PaymentApplicationService
import com.arshalif.cashi.core.remote.ApiResponse
import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.payment.data.model.PaymentRequestDto
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.features.payment.domain.validation.ValidationResult
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.datetime.Clock

fun Route.paymentRoutes(
    paymentService: PaymentApplicationService
) {
    route("/payments") {
        post {
            try {
                // Parse request using proper JSON deserialization
                val request = call.receive<PaymentRequestDto>()

                // Create domain object for validation
                val payment = Payment(
                    recipientEmail = request.recipientEmail,
                    amount = request.amount,
                    currency = try {
                        Currency.valueOf(request.currency.uppercase())
                    } catch (e: IllegalArgumentException) {
                        val errorResponse = ApiResponse<Nothing>(
                            success = false,
                            message = "Payment validation failed",
                            error = "Unsupported currency: ${request.currency}"
                        )
                        call.respond(HttpStatusCode.BadRequest, errorResponse)
                        return@post
                    },
                    timestamp = Clock.System.now()
                )
                
                // Process payment
                val paymentResponse: NetworkResult<Payment> = paymentService.processPayment(payment)

                when (paymentResponse) {
                    is NetworkResult.Error -> {
                        paymentResponse.message
                        val errorResponse = ApiResponse<Nothing>(
                            success = false,
                            message = "Payment processing failed: ${paymentResponse.message}",
                            error = paymentResponse.message
                        )
                        call.respond(HttpStatusCode.BadRequest, errorResponse)
                        return@post
                    }
                    is NetworkResult.Success -> {
                        // Create DTO for response (using timestamp-based ID like the repository)
                        val paymentDto = PaymentDto(
                            id = paymentResponse.data.timestamp.toEpochMilliseconds().toString(),
                            recipientEmail = paymentResponse.data.recipientEmail,
                            amount = paymentResponse.data.amount,
                            currency = paymentResponse.data.currency.code,
                            timestamp = paymentResponse.data.timestamp.toString()
                        )
                        val successResponse = ApiResponse(
                            success = true,
                            message = "Payment processed successfully",
                            data = paymentDto
                        )
                        call.respond(HttpStatusCode.Created, successResponse)
                    }
                    is NetworkResult.Loading -> {
                    }
                }

                
            } catch (e: Exception) {
                val errorResponse = ApiResponse<PaymentDto>(
                    success = false,
                    message = "Internal server error",
                    error = e.message ?: "Unknown error"
                )
                call.respond(HttpStatusCode.InternalServerError, errorResponse)
            }
        }
    }
} 