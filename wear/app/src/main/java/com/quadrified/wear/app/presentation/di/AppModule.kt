package com.quadrified.wear.app.presentation.di

import com.quadrified.wear.app.presentation.RuniqueApp
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
        // Providing applicationScope
    single {
        (androidApplication() as RuniqueApp).applicationScope
    }
}