package com.quadrified.run.presentation.di

import com.quadrified.run.presentation.active_run.ActiveRunViewModel
import com.quadrified.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val runViewModelModule = module {
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}