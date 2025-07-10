package com.arshalif.cashi.di

import com.arshalif.cashi.features.payment.domain.repository.PaymentRepository
import com.arshalif.cashi.features.transaction.domain.repository.TransactionRepository
import com.arshalif.cashi.repository.ServerPaymentRepository
import com.arshalif.cashi.repository.ServerTransactionRepository
import org.koin.dsl.module

val repositoryModule = module {
    // Repositories
    single<PaymentRepository> { ServerPaymentRepository(get()) }
    single<TransactionRepository> { ServerTransactionRepository(get()) }
} 