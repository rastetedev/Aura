package com.raulastete.aura.screens.create_record

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val createRecordModule = module {
    viewModelOf(::CreateRecordViewModel)
}