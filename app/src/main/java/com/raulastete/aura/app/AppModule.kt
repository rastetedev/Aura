package com.raulastete.aura.app

import androidx.room3.Room
import com.raulastete.aura.core.database.AuraDatabase
import com.raulastete.aura.features.player.AudioPlayer
import com.raulastete.aura.features.player.data.AndroidAudioPlayer
import com.raulastete.aura.features.record.data.RoomRecordDataSource
import com.raulastete.aura.features.recording.data.InternalRecordingStorage
import com.raulastete.aura.features.settings.data.DataStoreSettings
import com.raulastete.aura.screens.create_record.CreateRecordViewModel
import com.raulastete.aura.screens.record_list.RecordListViewModel
import com.raulastete.aura.screens.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {

    single<CoroutineScope>{
        (androidApplication() as AuraApp).applicationScope
    }

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

    singleOf(::AndroidAudioPlayer) bind AudioPlayer::class

    single {
        com.raulastete.aura.features.recording.data.AndroidVoiceRecorder(
            context = androidApplication(),
            applicationScope = get()
        )
    } bind com.raulastete.aura.features.recording.VoiceRecorder::class

    singleOf(::InternalRecordingStorage) bind com.raulastete.aura.features.recording.RecordingStorage::class

    singleOf(::RoomRecordDataSource) bind com.raulastete.aura.features.record.RecordDataSource::class
    singleOf(::DataStoreSettings) bind com.raulastete.aura.features.settings.SettingsPreferences::class

    viewModelOf(::RecordListViewModel)
    viewModelOf(::CreateRecordViewModel)
    viewModelOf(::SettingsViewModel)
}