package com.arshalif.cashi.features.payment.domain.validation.currency

import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.validation.currency.strategies.*

class CurrencyValidationStrategyFactory {
    private val strategies = mapOf(
        Currency.USD to USDValidationStrategy(),
        Currency.EUR to EURValidationStrategy(),
        Currency.GBP to GBPValidationStrategy()
    )

    fun getStrategy(currency: Currency): CurrencyValidationStrategy? {
        return strategies[currency]
    }

    fun isSupported(currency: Currency): Boolean {
        return strategies.containsKey(currency)
    }

    fun getSupportedCurrencies(): List<Currency> {
        return strategies.keys.toList()
    }
} 