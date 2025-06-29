package com.arshalif.cashi.config.di

import com.arshalif.cashi.features.payment.domain.repository.PaymentRepository
import com.arshalif.cashi.features.transaction.domain.repository.TransactionRepository
import com.arshalif.cashi.infrastructure.repository.ServerPaymentRepository
import com.arshalif.cashi.infrastructure.repository.ServerTransactionRepository
import org.koin.dsl.module

val repositoryModule = module {
    // Repositories
    single<PaymentRepository> { ServerPaymentRepository(get()) }
    single<TransactionRepository> { ServerTransactionRepository(get()) }
} 