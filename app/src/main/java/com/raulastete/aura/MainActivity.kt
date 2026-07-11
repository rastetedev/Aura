package com.raulastete.aura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.raulastete.aura.core.presentation.designsystem.buttons.PrimaryButton
import com.raulastete.aura.core.presentation.designsystem.dropdowns.ExtraDropdownOption
import com.raulastete.aura.core.presentation.designsystem.dropdowns.SelectableDropdown
import com.raulastete.aura.core.presentation.designsystem.dropdowns.asUnselectedItems
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraTheme {
                SelectableDropdown(
                    items = (1..5).map { "Option number $it" }.asUnselectedItems()
                        .map { if (it.item == "Option number 3") it.copy(selected = true) else it },
                    itemDisplayText = { it },
                    itemKey = { it },
                    onDismiss = {},
                    onItemClick = {},
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.hashtag),
                            contentDescription = null
                        )
                    },
                    extraDropdownOption = ExtraDropdownOption(
                        text = "Add a new hashtag",
                        onClick = {}
                    )
                )
            }
        }
    }
}
