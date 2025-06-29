package com.arshalif.cashi

import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.validation.DefaultCurrencyValidator
import kotlin.test.Test

class SimpleDebugTest {
    
    @Test
    fun `debug currency validation`() {
        val validator = DefaultCurrencyValidator()
        
        println("Testing EUR 9000.0:")
        val eurResult = validator.validateAmount(9000.0, Currency.EUR)
        println("Result: $eurResult")
        
        println("Testing GBP 9000.0:")
        val gbpResult = validator.validateAmount(9000.0, Currency.GBP)
        println("Result: $gbpResult")
        
        println("Testing EUR 8500.0:")
        val eurResult2 = validator.validateAmount(8500.0, Currency.EUR)
        println("Result: $eurResult2")
        
        println("Testing GBP 8000.0:")
        val gbpResult2 = validator.validateAmount(8000.0, Currency.GBP)
        println("Result: $gbpResult2")
    }
} 