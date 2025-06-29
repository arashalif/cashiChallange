package com.arshalif.cashi.config.di

import org.koin.dsl.module

// Combine all modules for production
val appModules = listOf(
    infrastructureModule,
    repositoryModule,
    domainModule,
    applicationModule
) 