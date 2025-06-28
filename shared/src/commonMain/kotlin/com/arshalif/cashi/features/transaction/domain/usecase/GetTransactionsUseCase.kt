package com.arshalif.cashi.features.transaction.domain.usecase

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.transaction.domain.model.Transaction
import com.arshalif.cashi.features.transaction.domain.repository.TransactionRepository

class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(): NetworkResult<List<Transaction>> {
        return transactionRepository.getTransactions()
    }
} 