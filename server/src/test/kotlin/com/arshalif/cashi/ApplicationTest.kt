package com.arshalif.cashi

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Cashi Payment API Server is running!", bodyAsText())
        }
    }
    
    @Test
    fun testHealthEndpoint() = testApplication {
        application {
            module()
        }
        client.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("healthy"))
        }
    }
    
    @Test
    fun testPaymentEndpointValidRequest() = testApplication {
        application {
            module()
        }
        
        val paymentRequest = """
            {
                "recipientEmail": "test@example.com",
                "amount": 100.0,
                "currency": "USD"
            }
        """.trimIndent()
        
        client.post("/payments") {
            contentType(ContentType.Application.Json)
            setBody(paymentRequest)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            assertTrue(bodyAsText().contains("Payment processed successfully"))
        }
    }
    
    @Test
    fun testPaymentEndpointInvalidEmail() = testApplication {
        application {
            module()
        }
        
        val paymentRequest = """
            {
                "recipientEmail": "invalid-email",
                "amount": 100.0,
                "currency": "USD"
            }
        """.trimIndent()
        
        client.post("/payments") {
            contentType(ContentType.Application.Json)
            setBody(paymentRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertTrue(bodyAsText().contains("Payment validation failed"))
        }
    }
    
    @Test
    fun testPaymentEndpointInvalidAmount() = testApplication {
        application {
            module()
        }
        
        val paymentRequest = """
            {
                "recipientEmail": "test@example.com",
                "amount": -100.0,
                "currency": "USD"
            }
        """.trimIndent()
        
        client.post("/payments") {
            contentType(ContentType.Application.Json)
            setBody(paymentRequest)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertTrue(bodyAsText().contains("Payment validation failed"))
        }
    }
    
    @Test
    fun testTransactionsEndpoint() = testApplication {
        application {
            module()
        }
        
        // First create a payment
        val paymentRequest = """
            {
                "recipientEmail": "test@example.com",
                "amount": 100.0,
                "currency": "USD"
            }
        """.trimIndent()
        
        client.post("/payments") {
            contentType(ContentType.Application.Json)
            setBody(paymentRequest)
        }
        
        // Then get transactions
        client.get("/transactions").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("success"))
        }
    }
}