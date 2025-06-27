package com.arshalif.cashi.features.payment.domain.validation.currency

import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.validation.ValidationResult

interface CurrencyValidationStrategy {
    fun validateAmount(amount: Double): ValidationResult
    fun getCurrency(): Currency
} 