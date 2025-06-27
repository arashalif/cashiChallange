package com.arshalif.cashi.features.payment.domain.validation.currency.strategies

import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.validation.ValidationResult
import com.arshalif.cashi.features.payment.domain.validation.currency.CurrencyValidationStrategy

class USDValidationStrategy : CurrencyValidationStrategy {
    override fun validateAmount(amount: Double): ValidationResult {
        return when {
            amount < 0.01 -> ValidationResult.Invalid("Amount must be at least $0.01")
            amount > 10000.0 -> ValidationResult.Invalid("Amount exceeds maximum allowed of $10,000")
            else -> ValidationResult.Valid
        }
    }

    override fun getCurrency(): Currency = Currency.USD
} 