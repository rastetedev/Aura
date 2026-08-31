package com.raulastete.aura.app

import androidx.room3.Room
import com.raulastete.aura.BuildConfig
import com.raulastete.aura.core.database.AuraDatabase
import com.raulastete.aura.core.features.player.AudioPlayer
import com.raulastete.aura.core.features.player.data.AndroidAudioPlayer
import com.raulastete.aura.core.features.record.RecordDataSource
import com.raulastete.aura.core.features.record.data.RoomRecordDataSource
import com.raulastete.aura.core.features.recording.RecordingStorage
import com.raulastete.aura.core.features.recording.VoiceRecorder
import com.raulastete.aura.core.features.recording.data.AndroidVoiceRecorder
import com.raulastete.aura.core.features.recording.data.InternalRecordingStorage
import com.raulastete.aura.core.features.settings.SettingsPreferences
import com.raulastete.aura.core.features.settings.data.DataStoreSettings
import com.raulastete.aura.core.features.statistics.CalculateMoodFrequenciesUseCase
import com.raulastete.aura.core.features.statistics.CalculateMoodHeatMapUseCase
import com.raulastete.aura.screens.create_record.CreateRecordViewModel
import com.raulastete.aura.screens.record_list.RecordListViewModel
import com.raulastete.aura.screens.settings.SettingsViewModel
import com.raulastete.aura.screens.statistics.StatisticsViewModel
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
        ).apply {
            if (BuildConfig.DEBUG) {
                createFromAsset("database/aura_debug.db")
            }
        }.build()
    }
    single {
        get<AuraDatabase>().recordDao
    }

    singleOf(::AndroidAudioPlayer) bind AudioPlayer::class

    single {
        AndroidVoiceRecorder(
            context = androidApplication(),
            applicationScope = get()
        )
    } bind VoiceRecorder::class

    singleOf(::InternalRecordingStorage) bind RecordingStorage::class

    singleOf(::RoomRecordDataSource) bind RecordDataSource::class
    singleOf(::DataStoreSettings) bind SettingsPreferences::class

    singleOf(::CalculateMoodHeatMapUseCase)
    singleOf(::CalculateMoodFrequenciesUseCase)

    viewModelOf(::RecordListViewModel)
    viewModelOf(::CreateRecordViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::StatisticsViewModel)
}