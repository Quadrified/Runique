package com.quadrified.analytics.data.di

import com.quadrified.analytics.data.RoomAnalyticsRepository
import com.quadrified.analytics.domain.AnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
}