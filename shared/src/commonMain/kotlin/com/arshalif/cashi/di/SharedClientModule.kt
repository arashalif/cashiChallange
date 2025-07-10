package com.arshalif.cashi.di

import com.arshalif.cashi.core.config.ApiConfig
import com.arshalif.cashi.core.remote.ApiService
import com.arshalif.cashi.core.remote.DefaultApiService
import com.arshalif.cashi.features.payment.data.repository.PaymentRepositoryImpl
import com.arshalif.cashi.features.payment.domain.repository.PaymentRepository
import com.arshalif.cashi.features.payment.domain.usecase.SendPaymentUseCase
import com.arshalif.cashi.features.payment.domain.validation.CurrencyValidator
import com.arshalif.cashi.features.payment.domain.validation.DefaultCurrencyValidator
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.features.transaction.data.repository.TransactionRepositoryImpl
import com.arshalif.cashi.features.transaction.domain.repository.TransactionRepository
import com.arshalif.cashi.features.transaction.domain.usecase.GetTransactionsUseCase
import org.koin.dsl.module

val sharedClientModule = module {

    // API Services
    single<ApiService> {
        DefaultApiService(
            baseUrl = ApiConfig.BASE_URL
        )
    }

    // Repositories
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }

    // Validators
    single<CurrencyValidator> { DefaultCurrencyValidator() }
    single<PaymentValidator> { PaymentValidator(get()) }

    // Use Cases
    single<SendPaymentUseCase> { SendPaymentUseCase(get(), get()) }
    single<GetTransactionsUseCase> { GetTransactionsUseCase(get()) }

}