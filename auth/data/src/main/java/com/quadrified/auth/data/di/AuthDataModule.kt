package com.quadrified.auth.data.di

import com.quadrified.auth.data.EmailPatternValidator
import com.quadrified.auth.domain.PatternValidator
import com.quadrified.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
}