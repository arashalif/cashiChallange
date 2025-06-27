package com.arshalif.cashi.features.payment.domain.validation

import com.arshalif.cashi.features.payment.domain.model.Currency
import kotlin.test.Test
import kotlin.test.assertTrue

class CurrencyValidationStrategyTest {
    private val validator = DefaultCurrencyValidator()

    @Test
    fun `strategy pattern supports all currencies`() {
        // Test that all currencies are supported
        assertTrue(validator.isSupported(Currency.USD))
        assertTrue(validator.isSupported(Currency.EUR))
        assertTrue(validator.isSupported(Currency.GBP))
    }

    @Test
    fun `each currency has its own validation rules`() {
        // USD allows up to $10,000
        val usdResult = validator.validateAmount(15000.0, Currency.USD)
        assertTrue(usdResult is ValidationResult.Invalid)
        assertTrue((usdResult as ValidationResult.Invalid).message.contains("$10,000"))

        // EUR allows up to €8,500
        val eurResult = validator.validateAmount(9000.0, Currency.EUR)
        assertTrue(eurResult is ValidationResult.Invalid)
        assertTrue((eurResult as ValidationResult.Invalid).message.contains("€8,500"))

        // GBP allows up to £8,000
        val gbpResult = validator.validateAmount(9000.0, Currency.GBP)
        assertTrue(gbpResult is ValidationResult.Invalid)
        assertTrue((gbpResult as ValidationResult.Invalid).message.contains("£8,000"))
    }

    @Test
    fun `all currencies follow their own validation rules`() {
        // USD minimum amount
        val usdMinResult = validator.validateAmount(0.005, Currency.USD)
        assertTrue(usdMinResult is ValidationResult.Invalid)
        assertTrue((usdMinResult as ValidationResult.Invalid).message.contains("$0.01"))

        // EUR maximum amount
        val eurMaxResult = validator.validateAmount(9000.0, Currency.EUR)
        assertTrue(eurMaxResult is ValidationResult.Invalid)
        assertTrue((eurMaxResult as ValidationResult.Invalid).message.contains("€8,500"))

        // GBP maximum amount
        val gbpMaxResult = validator.validateAmount(9000.0, Currency.GBP)
        assertTrue(gbpMaxResult is ValidationResult.Invalid)
        assertTrue((gbpMaxResult as ValidationResult.Invalid).message.contains("£8,000"))

        // Valid amounts for all currencies
        assertTrue(validator.validateAmount(5000.0, Currency.USD) is ValidationResult.Valid)
        assertTrue(validator.validateAmount(5000.0, Currency.EUR) is ValidationResult.Valid)
        assertTrue(validator.validateAmount(5000.0, Currency.GBP) is ValidationResult.Valid)
    }
} 