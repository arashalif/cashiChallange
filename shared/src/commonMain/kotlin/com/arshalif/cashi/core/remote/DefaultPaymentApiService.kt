package com.arshalif.cashi.core.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class DefaultPaymentApiService(
    private val baseUrl: String,
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
) : PaymentApiService {
    
    override suspend fun sendPayment(request: com.arshalif.cashi.features.payment.data.model.PaymentRequestDto): PaymentResponse {
        return try {
            val response: HttpResponse = httpClient.post("$baseUrl/payments") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            when (response.status) {
                HttpStatusCode.Created -> {
                    response.body<PaymentResponse>()
                }
                HttpStatusCode.BadRequest -> {
                    val errorResponse = response.body<PaymentResponse>()
                    PaymentResponse(
                        success = false,
                        message = errorResponse.message ?: "Bad request",
                        error = errorResponse.error
                    )
                }
                else -> {
                    PaymentResponse(
                        success = false,
                        message = "Server error",
                        error = "HTTP ${response.status.value}: ${response.status.description}"
                    )
                }
            }
        } catch (e: Exception) {
            PaymentResponse(
                success = false,
                message = "Network error",
                error = e.message ?: "Unknown error"
            )
        }
    }
    
    override suspend fun getTransactionHistory(): TransactionsResponse {
        return try {
            val response: HttpResponse = httpClient.get("$baseUrl/transactions") {
                contentType(ContentType.Application.Json)
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    response.body<TransactionsResponse>()
                }
                else -> {
                    TransactionsResponse(
                        success = false,
                        transactions = emptyList(),
                        error = "HTTP ${response.status.value}: ${response.status.description}"
                    )
                }
            }
        } catch (e: Exception) {
            TransactionsResponse(
                success = false,
                transactions = emptyList(),
                error = e.message ?: "Unknown error"
            )
        }
    }
} 