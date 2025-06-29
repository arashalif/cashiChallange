package com.arshalif.cashi

import com.arshalif.cashi.application.service.PaymentApplicationService
import com.arshalif.cashi.application.service.TransactionApplicationService
import com.arshalif.cashi.config.di.appModules
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.presentation.routes.healthRoutes
import com.arshalif.cashi.presentation.routes.paymentRoutes
import com.arshalif.cashi.presentation.routes.transactionRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
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
    
    // Initialize Koin
    startKoin {
        modules(appModules)
    }
    
    // Inject dependencies
    val paymentApplicationService by inject<PaymentApplicationService>()
    val transactionApplicationService by inject<TransactionApplicationService>()
    val paymentValidator by inject<PaymentValidator>()
    
    // Configure routing
    routing {
        // Payment routes
        paymentRoutes(
            paymentService = paymentApplicationService,
            paymentValidator = paymentValidator
        )
        
        // Transaction routes
        transactionRoutes(
            transactionService = transactionApplicationService
        )
        
        // Health check routes
        healthRoutes()
    }
} 