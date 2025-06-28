package com.arshalif.cashi.di

import com.arshalif.cashi.core.config.ApiConfig
import com.arshalif.cashi.core.remote.DefaultPaymentApiService
import com.arshalif.cashi.core.remote.PaymentApiService
import com.arshalif.cashi.features.payment.data.repository.PaymentRepositoryImpl
import com.arshalif.cashi.features.payment.domain.repository.PaymentRepository
import com.arshalif.cashi.features.payment.domain.usecase.SendPaymentUseCase
import com.arshalif.cashi.features.payment.domain.validation.CurrencyValidator
import com.arshalif.cashi.features.payment.domain.validation.DefaultCurrencyValidator
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.features.transaction.data.repository.TransactionRepositoryImpl
import com.arshalif.cashi.features.transaction.domain.repository.TransactionRepository
import com.arshalif.cashi.features.transaction.domain.usecase.GetTransactionsUseCase
import com.arshalif.cashi.presentation.screen.payment.PaymentViewModel
import com.arshalif.cashi.presentation.screen.transaction.TransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    
    // API Services
    single<PaymentApiService> { 
        DefaultPaymentApiService(
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
    
    // ViewModels
    viewModel { PaymentViewModel(get(), get()) }
    viewModel { TransactionViewModel(get()) }
} 