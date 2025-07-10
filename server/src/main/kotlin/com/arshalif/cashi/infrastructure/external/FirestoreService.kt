package com.arshalif.cashi.infrastructure.external

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class FirestoreService : FirestoreServiceInterface {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }
    
    private val projectId = "cashi-3e4bd"
    private val baseUrl = "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents"
    
    override suspend fun savePayment(payment: PaymentDto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            println("Saving payment to Firestore transactions collection: ${payment.id}")
            
            // Create properly serialized request
            val firestoreRequest = FirestoreCreateRequest(
                fields = FirestoreDocumentFields(
                    id = FirestoreStringValue(payment.id),
                    recipientEmail = FirestoreStringValue(payment.recipientEmail),
                    amount = FirestoreDoubleValue(payment.amount),
                    currency = FirestoreStringValue(payment.currency),
                    timestamp = FirestoreStringValue(payment.timestamp)
                )
            )
            
            val response: HttpResponse = httpClient.post("$baseUrl/transactions") {
                contentType(ContentType.Application.Json)
                setBody(firestoreRequest)
            }
            
            if (response.status.isSuccess()) {
                println("Payment saved to Firestore transactions collection: ${payment.id}")
                Result.success(Unit)
            } else {
                val errorBody = response.body<String>()
                println("!!! Failed to save payment to Firestore: ${response.status} - $errorBody")
                Result.failure(Exception("HTTP ${response.status.value}: $errorBody"))
            }
        } catch (e: Exception) {
            println("!!! Error saving payment to Firestore: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getTransactions(): Result<List<TransactionDto>> = withContext(Dispatchers.IO) {
        try {
            println("Retrieving transactions from Firestore...")
            
            val response: HttpResponse = httpClient.get("$baseUrl/transactions") {
                contentType(ContentType.Application.Json)
            }
            
            if (response.status.isSuccess()) {
                val responseBody = response.body<String>()
                println("Firestore response: $responseBody")
                
                // Parse using proper deserialization
                val firestoreResponse = try {
                    json.decodeFromString<FirestoreListResponse>(responseBody)
                } catch (e: Exception) {
                    println("Failed to parse Firestore response: ${e.message}")
                    // Return empty response if parsing fails
                    FirestoreListResponse(emptyList())
                }
                
                // Convert Firestore documents to TransactionDto
                val transactions = firestoreResponse.documents.mapNotNull { document ->
                    try {
                        TransactionDto(
                            id = document.fields.id.stringValue,
                            recipientEmail = document.fields.recipientEmail.stringValue,
                            amount = document.fields.amount.doubleValue,
                            currency = document.fields.currency.stringValue,
                            timestamp = document.fields.timestamp.stringValue
                        )
                    } catch (e: Exception) {
                        println("Error converting Firestore document to TransactionDto: ${e.message}")
                        null // Skip invalid documents
                    }
                }
                
                // Sort transactions by timestamp (newest first)
                val sortedTransactions = transactions.sortedByDescending { it.timestamp }
                println("Retrieved ${transactions.size} transactions from Firestore")
                Result.success(sortedTransactions)
            } else {
                val errorBody = response.body<String>()
                println("!!! Failed to get transactions from Firestore: ${response.status} - $errorBody")
                Result.failure(Exception("HTTP ${response.status.value}: $errorBody"))
            }
        } catch (e: Exception) {
            println("!!! Error retrieving transactions from Firestore: ${e.message}")
            Result.failure(e)
        }
    }
} 