package com.quadrified.run.di

import com.quadrified.core.domain.run.SyncRunScheduler
import com.quadrified.run.connectivity.PhoneToWatchConnector
import com.quadrified.run.data.CreateRunWorker
import com.quadrified.run.data.DeleteRunWorker
import com.quadrified.run.data.FetchRunsWorker
import com.quadrified.run.data.SyncRunWorkerScheduler
import com.quadrified.run.domain.WatchConnector
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
    singleOf(::PhoneToWatchConnector).bind<WatchConnector>()
}