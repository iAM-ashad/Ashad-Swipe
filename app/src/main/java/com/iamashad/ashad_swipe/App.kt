package com.iamashad.ashad_swipe

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.iamashad.ashad_swipe.di.databaseModule
import com.iamashad.ashad_swipe.di.networkModule
import com.iamashad.ashad_swipe.di.repositoryModule
import com.iamashad.ashad_swipe.di.viewModelModule
import com.iamashad.ashad_swipe.di.workModule
import com.iamashad.ashad_swipe.util.NotificationHelper
import com.iamashad.ashad_swipe.work.UploadPendingWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.context.startKoin

/**
 * Application entry point.
 * - Initializes Koin DI modules.
 * - Configures WorkManager with KoinWorkerFactory.
 * - Ensures notification channel is ready.
 * - Starts periodic pending upload worker.
 */

class App : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration by lazy {
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(KoinWorkerFactory()) // Koin-aware workers
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                networkModule,
                databaseModule,
                repositoryModule,
                viewModelModule,
                workModule
            )
        }

        NotificationHelper.ensureChannel(this)
        WorkManager.getInstance(this).pruneWork()
        WorkManager.getInstance(this).cancelAllWorkByTag(UploadPendingWorker.TAG)
        UploadPendingWorker.schedule(this)
    }
}
