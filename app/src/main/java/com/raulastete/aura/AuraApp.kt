package com.raulastete.aura

import android.app.Application
import com.raulastete.aura.screens.create_record.createRecordModule
import com.raulastete.aura.screens.record_list.recordListModule
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

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@AuraApp)
            modules(
                appModule, recordListModule, createRecordModule
            )
        }
    }
}