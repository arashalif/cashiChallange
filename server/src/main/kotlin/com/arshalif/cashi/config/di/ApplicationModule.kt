package com.arshalif.cashi.config.di

import com.arshalif.cashi.application.service.PaymentApplicationService
import com.arshalif.cashi.application.service.TransactionApplicationService
import org.koin.dsl.module

val applicationModule = module {
    // Application Services
    single { PaymentApplicationService(get()) }
    single { TransactionApplicationService(get(), get()) }
} 