package com.arshalif.cashi.features.transaction.domain.repository

import com.arshalif.cashi.features.transaction.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>
} 