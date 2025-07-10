package com.arshalif.cashi.service

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import com.arshalif.cashi.features.transaction.domain.usecase.GetTransactionsUseCase
import java.util.concurrent.ConcurrentHashMap

class TransactionApplicationService(
    private val getTransactionsUseCase: GetTransactionsUseCase
) {
    // In-memory storage for fallback
    private val transactions = ConcurrentHashMap<String, TransactionDto>()
    
    suspend fun getAllTransactions(): List<TransactionDto> {
        // Use the shared use case
        return when (val result = getTransactionsUseCase()) {
            is NetworkResult.Success -> {
                // Convert domain models back to DTOs for the API response
                result.data.map { transaction ->
                    TransactionDto(
                        id = transaction.id,
                        recipientEmail = transaction.recipientEmail,
                        amount = transaction.amount,
                        currency = transaction.currency.name,
                        timestamp = transaction.timestamp.toString()
                    )
                }
            }
            is NetworkResult.Error -> {
                println("!!! Failed to get transactions: ${result.message}")
                println("!!! Falling back to in-memory storage")
                // Fallback to in-memory storage (sorted by timestamp desc - newest first)
                transactions.values.toList().sortedByDescending { it.timestamp }
            }
            is NetworkResult.Loading -> {
                println("!!! Transactions are still loading, returning empty list")
                emptyList()
            }
        }
    }
} 