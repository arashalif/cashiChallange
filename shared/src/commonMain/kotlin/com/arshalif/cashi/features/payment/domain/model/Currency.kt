package com.arshalif.cashi.features.payment.domain.model

enum class Currency(val code: String, val symbol: String, val displayName: String) {
    USD("USD", "$", "US Dollar"),
    EUR("EUR", "€", "Euro"),
    GBP("GBP", "£", "British Pound");
    
    companion object {
        fun fromCode(code: String): Currency? = values().find { it.code == code.uppercase() }
        fun isSupported(code: String): Boolean = fromCode(code) != null
    }
} 