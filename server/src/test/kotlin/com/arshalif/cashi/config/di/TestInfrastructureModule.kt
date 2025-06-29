package com.arshalif.cashi.config.di

import com.arshalif.cashi.infrastructure.external.FirestoreServiceInterface
import com.arshalif.cashi.infrastructure.external.MockFirestoreService
import org.koin.dsl.module

val testInfrastructureModule = module {
    // External Services - Using MOCK for tests
    single<FirestoreServiceInterface> { MockFirestoreService() }
} 