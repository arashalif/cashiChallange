package com.arshalif.cashi.di

import com.arshalif.cashi.service.PaymentApplicationService
import com.arshalif.cashi.service.TransactionApplicationService
import org.koin.dsl.module

val applicationModule = module {
    // Application Services
    single { PaymentApplicationService(get()) }
    single { TransactionApplicationService(get()) }
} 