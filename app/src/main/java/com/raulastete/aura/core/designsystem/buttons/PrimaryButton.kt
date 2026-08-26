package com.raulastete.aura.core.designsystem.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.raulastete.aura.core.designsystem.theme.AuraTheme
import com.raulastete.aura.core.designsystem.theme.buttonGradient

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
) {

    val buttonTextColor by animateColorAsState(
        if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
    )

    Button(
        onClick = onClick,
        modifier = modifier.background(
            brush = if (enabled) MaterialTheme.colorScheme.buttonGradient
            else SolidColor(MaterialTheme.colorScheme.surfaceVariant) ,
            shape = CircleShape
        ),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        shape = CircleShape
    ) {
        leadingIcon?.invoke()
        if(leadingIcon != null){
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = buttonTextColor
        )
    }
}

data class ButtonState(
    val text: String,
    val isEnabled: Boolean,
    val leadingIcon: @Composable (() -> Unit)? = null
)

class ButtonStatePreviewParameter : PreviewParameterProvider<ButtonState> {

    private val buttonStateList = listOf(
        ButtonState(
            text = "Primary Button",
            isEnabled = true,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
            }
        ),
        ButtonState(
            text = "Primary Button",
            isEnabled = false,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
            }
        )
    )

    override val values: Sequence<ButtonState> = buttonStateList.asSequence()

    override fun getDisplayName(index: Int): String? {
        val state = buttonStateList.getOrNull(index) ?: return null
        return "Button is enabled: ${state.isEnabled}"
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview(
    @PreviewParameter(ButtonStatePreviewParameter::class) buttonState: ButtonState
) {
    AuraTheme {
        PrimaryButton(
            text = buttonState.text,
            onClick = {},
            enabled = buttonState.isEnabled,
            leadingIcon = buttonState.leadingIcon
        )
    }
}