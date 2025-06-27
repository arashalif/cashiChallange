package com.arshalif.cashi.features.transaction.data.repository

import com.arshalif.cashi.core.remote.PaymentApiService
import com.arshalif.cashi.features.transaction.data.model.toDomain
import com.arshalif.cashi.features.transaction.domain.model.Transaction
import com.arshalif.cashi.features.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TransactionRepositoryImpl(
    private val apiService: PaymentApiService
) : TransactionRepository {
    
    override fun getTransactions(): Flow<List<Transaction>> = flow {
        try {
            val transactionDtos = apiService.getTransactionHistory()
            val transactions = transactionDtos.map { it.toDomain() }
            emit(transactions)
        } catch (e: Exception) {
            emit(emptyList()) // In a real app, you might want to handle errors differently
        }
    }
} 