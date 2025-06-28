package com.arshalif.cashi

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import com.arshalif.cashi.features.payment.domain.model.Currency
import kotlinx.datetime.Clock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class PaymentService {
    
    // In-memory storage for demo purposes
    // In a real application, this would be replaced with a database
    private val payments = ConcurrentHashMap<String, PaymentDto>()
    private val transactions = ConcurrentHashMap<String, TransactionDto>()
    private val paymentIdCounter = AtomicLong(1)
    private val transactionIdCounter = AtomicLong(1)
    
    fun processPayment(
        recipientEmail: String,
        amount: Double,
        currency: String
    ): PaymentDto {
        val paymentId = paymentIdCounter.getAndIncrement().toString()
        val timestamp = Clock.System.now()
        val currencyEnum = Currency.valueOf(currency.uppercase())
        
        // Create payment
        val payment = PaymentDto(
            id = paymentId,
            recipientEmail = recipientEmail,
            amount = amount,
            currency = currencyEnum.name,
            timestamp = timestamp.toString()
        )
        
        // Store payment
        payments[paymentId] = payment
        
        // Create and store transaction
        val transactionId = transactionIdCounter.getAndIncrement().toString()
        val transaction = TransactionDto(
            id = transactionId,
            recipientEmail = recipientEmail,
            amount = amount,
            currency = currencyEnum.name,
            timestamp = timestamp.toString()
        )
        
        transactions[transactionId] = transaction
        
        // Simulate some processing time
        Thread.sleep(100)
        
        return payment
    }
    
    fun getTransactions(): List<TransactionDto> {
        return transactions.values.toList().sortedByDescending { it.timestamp }
    }
    
    fun getPayment(paymentId: String): PaymentDto? {
        return payments[paymentId]
    }
    
    fun getAllPayments(): List<PaymentDto> {
        return payments.values.toList().sortedByDescending { it.timestamp }
    }
    
    // Clear all data (useful for testing)
    fun clearAllData() {
        payments.clear()
        transactions.clear()
        paymentIdCounter.set(1)
        transactionIdCounter.set(1)
    }
    
    // Get statistics
    fun getStatistics(): Map<String, Any> {
        val totalPayments = payments.size
        val totalAmount = payments.values.sumOf { it.amount }
        val currencyBreakdown = payments.values
            .groupBy { it.currency }
            .mapValues { (_, payments) -> payments.sumOf { it.amount } }
        
        return mapOf(
            "totalPayments" to totalPayments,
            "totalAmount" to totalAmount,
            "currencyBreakdown" to currencyBreakdown,
            "lastUpdated" to Clock.System.now().toString()
        )
    }
} 