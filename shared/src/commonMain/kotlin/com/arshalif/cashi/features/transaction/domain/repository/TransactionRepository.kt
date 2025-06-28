package com.arshalif.cashi.features.transaction.domain.repository

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.transaction.domain.model.Transaction

interface TransactionRepository {
    suspend fun getTransactions(): NetworkResult<List<Transaction>>
} 