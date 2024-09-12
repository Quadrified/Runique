package com.quadrified.run.di

import com.quadrified.run.data.CreateRunWorker
import com.quadrified.run.data.DeleteRunWorker
import com.quadrified.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)
}