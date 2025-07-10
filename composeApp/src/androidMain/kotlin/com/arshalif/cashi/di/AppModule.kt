package com.arshalif.cashi.di

import com.arshalif.cashi.di.sharedClientModule
import com.arshalif.cashi.presentation.screen.payment.PaymentViewModel
import com.arshalif.cashi.presentation.screen.transaction.TransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    includes(sharedClientModule)

    // ViewModels
    viewModel { PaymentViewModel(get(), get()) }
    viewModel { TransactionViewModel(get()) }
} 