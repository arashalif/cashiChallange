package com.arshalif.cashi.core.remote

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.payment.data.model.PaymentRequestDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class DefaultApiService(
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
) : ApiService {
    
    override suspend fun sendPayment(request: PaymentRequestDto): ApiResponse<PaymentDto> {
        return try {
            val response: HttpResponse = httpClient.post("$baseUrl/payments") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            when (response.status) {
                HttpStatusCode.Created -> {
                    response.body<ApiResponse<PaymentDto>>()
                }
                HttpStatusCode.BadRequest -> {
                    val errorResponse = response.body<ApiResponse<PaymentDto>>()
                    ApiResponse(
                        success = false,
                        message = errorResponse.message ?: "Bad request",
                        error = errorResponse.error
                    )
                }
                else -> {
                    ApiResponse(
                        success = false,
                        message = "Server error",
                        error = "HTTP ${response.status.value}: ${response.status.description}"
                    )
                }
            }
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                message = "Network error",
                error = e.message ?: "Unknown error"
            )
        }
    }
    
    override suspend fun getTransactionHistory(): ApiResponse<List<TransactionDto>> {
        return try {
            val response: HttpResponse = httpClient.get("$baseUrl/transactions") {
                contentType(ContentType.Application.Json)
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    response.body<ApiResponse<List<TransactionDto>>>()
                }
                else -> {
                    ApiResponse(
                        success = false,
                        error = "HTTP ${response.status.value}: ${response.status.description}",
                        message = "Server error",
                    )
                }
            }
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                message = "Network error",
                error = e.message ?: "Unknown error",
            )
        }
    }
} 