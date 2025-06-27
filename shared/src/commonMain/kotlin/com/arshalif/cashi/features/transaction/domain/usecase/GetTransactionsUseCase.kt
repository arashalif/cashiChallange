package com.arshalif.cashi.features.transaction.domain.usecase

import com.arshalif.cashi.features.transaction.domain.model.Transaction
import com.arshalif.cashi.features.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return transactionRepository.getTransactions()
    }
} 