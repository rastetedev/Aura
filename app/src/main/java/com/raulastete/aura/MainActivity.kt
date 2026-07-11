package com.raulastete.aura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.raulastete.aura.core.presentation.designsystem.buttons.PrimaryButton
import com.raulastete.aura.core.presentation.designsystem.textfields.TransparentTextField
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val text = remember { mutableStateOf("") }
            AuraTheme {
                Column(
                    modifier = Modifier.fillMaxSize(
                    ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TransparentTextField(
                        modifier = Modifier.width(260.dp),
                        text = text.value,
                        onValueChange = {
                            text.value = it
                        },
                        hintText = "Hint text"
                    )
                    Spacer(Modifier.height(20.dp))
                    PrimaryButton(
                        modifier = Modifier.width(260.dp),
                        text = "Primary Button",
                        onClick = {},
                    )
                }
            }
        }
    }
}
