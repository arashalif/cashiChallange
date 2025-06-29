package com.arshalif.cashi.config.di

// Import production modules
import com.arshalif.cashi.config.di.repositoryModule
import com.arshalif.cashi.config.di.domainModule
import com.arshalif.cashi.config.di.applicationModule

// Combine modules for testing (using test infrastructure)
val testAppModules = listOf(
    testInfrastructureModule,  // Mock infrastructure
    repositoryModule,          // Same repositories
    domainModule,              // Same domain logic
    applicationModule          // Same application services
) 