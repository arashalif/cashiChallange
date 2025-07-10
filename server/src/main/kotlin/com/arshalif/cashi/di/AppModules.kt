package com.arshalif.cashi.di

// Combine all modules for production
val appModules = listOf(
    infrastructureModule,
    repositoryModule,
    domainModule,
    applicationModule
) 