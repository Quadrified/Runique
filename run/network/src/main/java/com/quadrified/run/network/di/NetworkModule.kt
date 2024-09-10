package com.quadrified.run.network.di

import com.quadrified.core.domain.run.RemoteRunDataSource
import com.quadrified.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()
}