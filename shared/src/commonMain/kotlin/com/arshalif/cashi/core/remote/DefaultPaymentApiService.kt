package com.arshalif.cashi.core.remote

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.payment.data.model.PaymentRequestDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
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
    
    override suspend fun sendPayment(request: PaymentRequestDto): PaymentDto {
        return httpClient.post("$baseUrl/payments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    override suspend fun getTransactionHistory(): List<TransactionDto> {
        return httpClient.get("$baseUrl/transactions") {
            contentType(ContentType.Application.Json)
        }.body()
    }
} 