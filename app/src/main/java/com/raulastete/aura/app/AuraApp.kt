package com.raulastete.aura.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.raulastete.aura.BuildConfig
import com.raulastete.aura.core.workers.AuraWorkerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class AuraApp : Application() {

    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@AuraApp)
            modules(appModule)
        }

        // Initialize WorkManager with our custom factory so Workers can use KoinComponent
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(AuraWorkerFactory())
                .build()
        )
    }
}