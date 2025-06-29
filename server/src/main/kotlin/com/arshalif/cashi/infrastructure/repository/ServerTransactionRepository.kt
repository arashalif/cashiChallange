package com.arshalif.cashi.infrastructure.repository

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.transaction.domain.model.Transaction
import com.arshalif.cashi.features.transaction.domain.repository.TransactionRepository
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.infrastructure.external.FirestoreServiceInterface
import kotlinx.datetime.Instant

class ServerTransactionRepository(
    private val firestoreService: FirestoreServiceInterface
) : TransactionRepository {
    
    override suspend fun getTransactions(): NetworkResult<List<Transaction>> {
        return try {
            firestoreService.getTransactions().fold(
                onSuccess = { transactionDtos ->
                    // Convert DTOs to domain models
                    val transactions = transactionDtos.map { dto ->
                        Transaction(
                            id = dto.id,
                            recipientEmail = dto.recipientEmail,
                            amount = dto.amount,
                            currency = try {
                                Currency.valueOf(dto.currency.uppercase())
                            } catch (e: IllegalArgumentException) {
                                Currency.USD // fallback to USD if currency is invalid
                            },
                            timestamp = try {
                                Instant.parse(dto.timestamp)
                            } catch (e: Exception) {
                                Instant.fromEpochMilliseconds(0) // fallback timestamp
                            }
                        )
                    }
                    NetworkResult.Success(transactions)
                },
                onFailure = { error ->
                    NetworkResult.Error("Failed to retrieve transactions: ${error.message}")
                }
            )
        } catch (e: Exception) {
            NetworkResult.Error("Transaction retrieval failed: ${e.message}")
        }
    }
} 