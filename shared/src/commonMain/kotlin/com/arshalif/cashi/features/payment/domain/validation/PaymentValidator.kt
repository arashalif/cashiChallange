package com.arshalif.cashi.features.payment.domain.validation

import com.arshalif.cashi.features.payment.domain.model.Payment

open class PaymentValidator(
    private val currencyValidator: CurrencyValidator = DefaultCurrencyValidator()
) {
    open fun isValidEmail(email: String): Boolean =
        email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))

    open fun validatePayment(payment: Payment): ValidationResult {
        return when {
            !isValidEmail(payment.recipientEmail) -> 
                ValidationResult.Invalid("Invalid email format")
            !currencyValidator.isSupported(payment.currency) -> 
                ValidationResult.Invalid("Unsupported currency: ${payment.currency.code}")
            else -> currencyValidator.validateAmount(payment.amount, payment.currency)
        }
    }
} 