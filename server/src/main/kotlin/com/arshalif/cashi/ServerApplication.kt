package com.arshalif.cashi

import com.arshalif.cashi.service.PaymentApplicationService
import com.arshalif.cashi.service.TransactionApplicationService
import com.arshalif.cashi.di.appModules
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.routes.healthRoutes
import com.arshalif.cashi.routes.paymentRoutes
import com.arshalif.cashi.routes.transactionRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.ktor.ext.inject

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Stop any existing Koin instance and start fresh
    if (GlobalContext.getOrNull() != null) {
        stopKoin()
    }
    
    // Configure JSON content negotiation
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        })
    }
    
    // Initialize Koin
    startKoin {
        modules(appModules)
    }
    
    // Inject dependencies
    val paymentApplicationService by inject<PaymentApplicationService>()
    val transactionApplicationService by inject<TransactionApplicationService>()
    
    // Configure routing
    routing {
        // Payment routes
        paymentRoutes(
            paymentService = paymentApplicationService,
        )
        
        // Transaction routes
        transactionRoutes(
            transactionService = transactionApplicationService
        )
        
        // Health check routes
        healthRoutes()
    }
} 