package com.arshalif.cashi.features.payment.domain.validation

import com.arshalif.cashi.features.payment.domain.model.Currency

interface CurrencyValidator {
    fun isSupported(currency: Currency): Boolean
    fun validateAmount(amount: Double, currency: Currency): ValidationResult
} 