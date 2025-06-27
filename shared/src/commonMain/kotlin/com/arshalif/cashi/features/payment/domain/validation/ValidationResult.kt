package com.arshalif.cashi.features.payment.domain.validation

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
    
    val isValid: Boolean get() = this is Valid
    val errorMessage: String? get() = if (this is Invalid) message else null
} 