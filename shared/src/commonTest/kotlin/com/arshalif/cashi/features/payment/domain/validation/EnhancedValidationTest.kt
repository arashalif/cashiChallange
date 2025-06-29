package com.arshalif.cashi.features.payment.domain.validation

import com.arshalif.cashi.features.payment.domain.model.Currency
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class EnhancedValidationTest {
    private val validator = DefaultCurrencyValidator()
    
    @Test
    fun `USD validation provides specific error messages`() {
        val tooSmall = validator.validateAmount(0.005, Currency.USD)
        assertTrue(tooSmall is ValidationResult.Invalid)
        assertTrue((tooSmall as ValidationResult.Invalid).message.contains("$0.01"))
        
        val tooLarge = validator.validateAmount(20000.0, Currency.USD)
        assertTrue(tooLarge is ValidationResult.Invalid)
        assertTrue((tooLarge as ValidationResult.Invalid).message.contains("$10,000"))
    }
    
    @Test
    fun `EUR validation provides specific error messages`() {
        val tooSmall = validator.validateAmount(0.005, Currency.EUR)
        assertTrue(tooSmall is ValidationResult.Invalid)
        assertTrue((tooSmall as ValidationResult.Invalid).message.contains("€0.01"))
        
        val tooLarge = validator.validateAmount(10000.0, Currency.EUR)
        assertTrue(tooLarge is ValidationResult.Invalid)
        assertTrue((tooLarge as ValidationResult.Invalid).message.contains("€8,500"))
    }
    
    @Test
    fun `GBP validation provides specific error messages`() {
        val tooSmall = validator.validateAmount(0.005, Currency.GBP)
        assertTrue(tooSmall is ValidationResult.Invalid)
        assertTrue((tooSmall as ValidationResult.Invalid).message.contains("£0.01"))
        
        val tooLarge = validator.validateAmount(10000.0, Currency.GBP)
        assertTrue(tooLarge is ValidationResult.Invalid)
        assertTrue((tooLarge as ValidationResult.Invalid).message.contains("£8,000"))
    }
    
    @Test
    fun `valid amounts return Valid result`() {
        val validUSD = validator.validateAmount(50.0, Currency.USD)
        assertTrue(validUSD is ValidationResult.Valid)
        
        val validEUR = validator.validateAmount(100.0, Currency.EUR)
        assertTrue(validEUR is ValidationResult.Valid)
        
        val validGBP = validator.validateAmount(150.0, Currency.GBP)
        assertTrue(validGBP is ValidationResult.Valid)
    }
} 