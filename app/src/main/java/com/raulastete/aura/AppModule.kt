package com.raulastete.aura

import com.raulastete.aura.core.data.audio.AndroidAudioPlayer
import com.raulastete.aura.core.data.record.RoomRecordDataSource
import com.raulastete.aura.core.data.settings.DataStoreSettings
import com.raulastete.aura.core.domain.audio.AudioPlayer
import com.raulastete.aura.core.domain.record.RecordDataSource
import com.raulastete.aura.core.domain.settings.SettingsPreferences
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope>{
        (androidApplication() as AuraApp).applicationScope
    }

    singleOf(::AndroidAudioPlayer) bind AudioPlayer::class

    singleOf(::RoomRecordDataSource) bind RecordDataSource::class

    singleOf(::DataStoreSettings) bind SettingsPreferences::class
}