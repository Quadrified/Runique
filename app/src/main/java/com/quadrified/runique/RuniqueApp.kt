package com.quadrified.runique

import android.app.Application
import com.quadrified.auth.data.di.authDataModule
import com.quadrified.auth.presentation.di.authViewModelModule
import com.quadrified.core.data.di.coreDataModule
import com.quadrified.core.database.di.databaseModule
import com.quadrified.run.location.di.locationModule
import com.quadrified.run.presentation.di.runPresentationModule
import com.quadrified.runique.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp : Application() {

    // Creating app-wide coroutineScope, independent coroutines
    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            modules(
                appModule,
                authDataModule,
                authViewModelModule,
                coreDataModule,
                runPresentationModule,
                locationModule,
                databaseModule
            )
        }
    }
}