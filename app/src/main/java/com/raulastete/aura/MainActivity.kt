package com.raulastete.aura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme
import com.raulastete.aura.screens.record_list.RecordListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            AuraTheme {
                RecordListScreen()
            }
        }
    }
}
