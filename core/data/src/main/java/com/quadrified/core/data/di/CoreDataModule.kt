package com.quadrified.core.data.di

import com.quadrified.core.data.auth.EncryptedSessionStorage
import com.quadrified.core.data.networking.HttpClientFactory
import com.quadrified.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get()).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
}