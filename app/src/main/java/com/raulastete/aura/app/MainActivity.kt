package com.raulastete.aura.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.raulastete.aura.core.designsystem.theme.AuraTheme
import com.raulastete.aura.app.navigation.NavigationRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            AuraTheme {
                NavigationRoot(
                    navController = rememberNavController(),
                )
            }
        }
    }
}
