package com.arshalif.cashi.infrastructure.external

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreService : FirestoreServiceInterface {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            // Use default JSON configuration
        }
    }
    
    private val projectId = "cashi-3e4bd"
    private val baseUrl = "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents"
    
    override suspend fun savePayment(payment: PaymentDto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            println("Saving payment to Firestore transactions collection: ${payment.id}")
            
            // Create JSON manually to avoid serialization issues
            // Save payment to transactions collection instead of payments
            val jsonBody = """
                {
                    "fields": {
                        "id": {"stringValue": "${payment.id}"},
                        "recipientEmail": {"stringValue": "${payment.recipientEmail}"},
                        "amount": {"doubleValue": ${payment.amount}},
                        "currency": {"stringValue": "${payment.currency}"},
                        "timestamp": {"stringValue": "${payment.timestamp}"}
                    }
                }
            """.trimIndent()
            
            val response: HttpResponse = httpClient.post("$baseUrl/transactions") {
                contentType(ContentType.Application.Json)
                setBody(jsonBody)
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
                
                // Parse the JSON response manually
                val transactions = mutableListOf<TransactionDto>()
                
                // Simple JSON parsing for documents array
                if (responseBody.contains("\"documents\"")) {
                    val documentsStart = responseBody.indexOf("\"documents\"")
                    val documentsContent = responseBody.substring(documentsStart)
                    
                    // Find each document
                    var currentIndex = 0
                    while (true) {
                        val docStart = documentsContent.indexOf("\"fields\"", currentIndex)
                        if (docStart == -1) break
                        
                        try {
                            // Extract fields for this document
                            val fieldsStart = documentsContent.indexOf("{", docStart)
                            val fieldsEnd = findMatchingBrace(documentsContent, fieldsStart)
                            val fieldsJson = documentsContent.substring(fieldsStart, fieldsEnd + 1)
                            
                            println("Parsing fields JSON: $fieldsJson")
                            
                            // Parse individual fields
                            val id = extractStringValue(fieldsJson, "id")
                            val recipientEmail = extractStringValue(fieldsJson, "recipientEmail")
                            val amount = extractDoubleValue(fieldsJson, "amount")
                            val currency = extractStringValue(fieldsJson, "currency")
                            val timestamp = extractStringValue(fieldsJson, "timestamp")
                            
                            println("Extracted values - id: $id, email: $recipientEmail, amount: $amount, currency: $currency, timestamp: $timestamp")
                            
                            if (id != null && recipientEmail != null && amount != null && currency != null && timestamp != null) {
                                transactions.add(TransactionDto(
                                    id = id,
                                    recipientEmail = recipientEmail,
                                    amount = amount,
                                    currency = currency,
                                    timestamp = timestamp
                                ))
                                println("Added transaction: $id")
                            } else {
                                println("Skipping transaction due to missing fields")
                            }
                            
                            currentIndex = fieldsEnd + 1
                        } catch (e: Exception) {
                            println("Error parsing transaction document: ${e.message}")
                            e.printStackTrace()
                            currentIndex = documentsContent.indexOf("\"fields\"", currentIndex + 1)
                            if (currentIndex == -1) break
                        }
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

    
    // Helper functions for JSON parsing
    private fun findMatchingBrace(json: String, startIndex: Int): Int {
        var braceCount = 0
        var index = startIndex
        
        while (index < json.length) {
            when (json[index]) {
                '{' -> braceCount++
                '}' -> {
                    braceCount--
                    if (braceCount == 0) {
                        return index
                    }
                }
            }
            index++
        }
        
        return json.length - 1 // fallback
    }
    
    private fun extractStringValue(json: String, fieldName: String): String? {
        val pattern = "\"$fieldName\"\\s*:\\s*\\{\\s*\"stringValue\"\\s*:\\s*\"([^\"]*)\""
        val regex = Regex(pattern)
        val match = regex.find(json)
        return match?.groupValues?.get(1)
    }
    
    private fun extractDoubleValue(json: String, fieldName: String): Double? {
        val pattern = "\"$fieldName\"\\s*:\\s*\\{\\s*\"doubleValue\"\\s*:\\s*([0-9.]+)"
        val regex = Regex(pattern)
        val match = regex.find(json)
        return match?.groupValues?.get(1)?.toDoubleOrNull()
    }
} 