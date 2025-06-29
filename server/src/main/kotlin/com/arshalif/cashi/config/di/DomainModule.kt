package com.arshalif.cashi.config.di

import com.arshalif.cashi.features.payment.domain.usecase.SendPaymentUseCase
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.features.transaction.domain.usecase.GetTransactionsUseCase
import org.koin.dsl.module

val domainModule = module {
    // Validators
    single { PaymentValidator() }
    
    // Use Cases
    single { SendPaymentUseCase(get(), get()) }
    single { GetTransactionsUseCase(get()) }
} 