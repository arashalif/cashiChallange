package com.arshalif.cashi.features.transaction.presentation.state

import com.arshalif.cashi.features.transaction.domain.model.Transaction

sealed class TransactionUiState {
    object Initial : TransactionUiState()
    object Loading : TransactionUiState()
    data class Success(val transactions: List<Transaction>) : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
} 