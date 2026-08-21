package com.raulastete.aura.screens.create_record

import com.raulastete.aura.core.data.recording.InternalRecordingStorage
import com.raulastete.aura.core.domain.recording.RecordingStorage
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val createRecordModule = module {
    viewModelOf(::CreateRecordViewModel)
    singleOf(::InternalRecordingStorage) bind RecordingStorage::class
}