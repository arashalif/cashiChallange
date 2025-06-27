package com.arshalif.cashi.features.payment.domain.validation

import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.validation.currency.CurrencyValidationStrategyFactory

class DefaultCurrencyValidator(
    private val strategyFactory: CurrencyValidationStrategyFactory = CurrencyValidationStrategyFactory()
) : CurrencyValidator {
    
    override fun isSupported(currency: Currency): Boolean {
        return strategyFactory.isSupported(currency)
    }

    override fun validateAmount(amount: Double, currency: Currency): ValidationResult {
        val strategy = strategyFactory.getStrategy(currency)
        return strategy?.validateAmount(amount) 
            ?: ValidationResult.Invalid("Unsupported currency: ${currency.code}")
    }
} 