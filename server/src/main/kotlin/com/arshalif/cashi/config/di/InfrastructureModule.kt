package com.arshalif.cashi.config.di

import com.arshalif.cashi.infrastructure.external.FirestoreService
import com.arshalif.cashi.infrastructure.external.FirestoreServiceInterface
import org.koin.dsl.module

val infrastructureModule = module {
    // External Services
    single<FirestoreServiceInterface> { FirestoreService() }
} 