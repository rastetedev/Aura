package com.raulastete.aura.core.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

/**
 * Custom WorkerFactory that lets Koin-backed Workers access injected dependencies.
 * Workers use [org.koin.core.component.KoinComponent] directly, so this factory
 * simply instantiates the correct class without needing to pass dependencies manually.
 */
class AuraWorkerFactory : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            ExportWorker::class.java.name -> ExportWorker(appContext, workerParameters)
            ImportWorker::class.java.name -> ImportWorker(appContext, workerParameters)
            else -> null // fall back to default factory
        }
    }
}
