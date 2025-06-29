package com.arshalif.cashi.features.payment.domain.validation

import com.arshalif.cashi.features.payment.domain.model.Currency
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class CurrencyValidatorTest {
    private val validator = DefaultCurrencyValidator()

    @Test
    fun `supported currencies pass validation`() {
        assertTrue(validator.isSupported(Currency.USD))
        assertTrue(validator.isSupported(Currency.EUR))
        assertTrue(validator.isSupported(Currency.GBP))
    }

    @Test
    fun `valid amounts for USD pass validation`() {
        val result1 = validator.validateAmount(10.0, Currency.USD)
        assertTrue(result1 is ValidationResult.Valid)
        
        val result2 = validator.validateAmount(0.01, Currency.USD)
        assertTrue(result2 is ValidationResult.Valid)
        
        val result3 = validator.validateAmount(9999.99, Currency.USD)
        assertTrue(result3 is ValidationResult.Valid)
    }

    @Test
    fun `amount below minimum fails validation`() {
        val result = validator.validateAmount(0.005, Currency.USD)
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("Amount must be at least"))
    }

    @Test
    fun `amount above maximum fails validation`() {
        val result = validator.validateAmount(15000.0, Currency.USD)
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("Amount exceeds maximum allowed"))
    }

    @Test
    fun `valid amounts for EUR pass validation`() {
        val result = validator.validateAmount(100.0, Currency.EUR)
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `EUR has different maximum amount than USD`() {
        val result = validator.validateAmount(9000.0, Currency.EUR)
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("8,500"))
    }

    @Test
    fun `GBP validation with its own limits`() {
        val result1 = validator.validateAmount(1000.0, Currency.GBP)
        assertTrue(result1 is ValidationResult.Valid)
        
        val result2 = validator.validateAmount(9000.0, Currency.GBP)
        assertTrue(result2 is ValidationResult.Invalid)
        assertTrue((result2 as ValidationResult.Invalid).message.contains("8,000"))
    }
} 