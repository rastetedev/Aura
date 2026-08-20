package com.raulastete.aura.screens.record_list

import com.raulastete.aura.core.data.recording.AndroidVoiceRecorder
import com.raulastete.aura.core.domain.recording.VoiceRecorder
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val recordListModule = module {
    single {
        AndroidVoiceRecorder(
            context = androidApplication(),
            applicationScope = get()
        )
    } bind VoiceRecorder::class

    viewModelOf(::RecordListViewModel)
}