package com.arshalif.cashi.features.transaction.data.repository

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.core.remote.ApiService
import com.arshalif.cashi.features.transaction.data.model.toDomain
import com.arshalif.cashi.features.transaction.domain.model.Transaction
import com.arshalif.cashi.features.transaction.domain.repository.TransactionRepository

class TransactionRepositoryImpl(
    private val apiService: ApiService
) : TransactionRepository {

    override suspend fun getTransactions(): NetworkResult<List<Transaction>> {
        return try {
            val response = apiService.getTransactionHistory()
            if (response.success) {
                val transactions = response.data?.map { it.toDomain() } ?: emptyList()
                NetworkResult.Success(transactions)
            } else {
                NetworkResult.Error(response.error ?: "Failed to fetch transactions")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch transactions")
        }
    }
} 