package com.arshalif.cashi.features.payment.presentation.state

import com.arshalif.cashi.features.payment.domain.model.Currency

data class PaymentFormState(
    val recipientEmail: String = "",
    val amount: String = "",
    val selectedCurrency: Currency = Currency.USD,
    val isEmailValid: Boolean = true,
    val isAmountValid: Boolean = true,
    val emailErrorMessage: String? = null,
    val amountErrorMessage: String? = null,
    val isLoading: Boolean = false
) {
    val isValid: Boolean
        get() = recipientEmail.isNotBlank() && 
                amount.isNotBlank() && 
                isEmailValid && 
                isAmountValid &&
                !isLoading
} 