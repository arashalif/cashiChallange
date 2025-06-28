package com.arshalif.cashi.features.payment.presentation.state

import com.arshalif.cashi.features.payment.domain.model.Payment

sealed class PaymentUiState {
    object Initial : PaymentUiState()
    object Loading : PaymentUiState()
    data class Success(val payment: Payment) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
} 