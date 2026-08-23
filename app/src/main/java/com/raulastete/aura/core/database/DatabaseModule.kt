package com.raulastete.aura.core.database

import androidx.room3.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import kotlin.jvm.java

val databaseModule = module {
    single<AuraDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AuraDatabase::class.java,
            "aura.db",
        ).build()
    }
    single {
        get<AuraDatabase>().recordDao
    }
}