package com.raulastete.aura.core.presentation.designsystem.buttons

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.raulastete.aura.core.presentation.designsystem.theme.buttonGradient
import com.raulastete.aura.core.presentation.designsystem.theme.buttonGradientPressed
import com.raulastete.aura.core.presentation.designsystem.theme.primary90
import com.raulastete.aura.core.presentation.designsystem.theme.primary95

@Composable
fun BubbleFabButton(
    showBubble: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    colors: BubbleFloatingActionButtonColors = rememberBubbleFloatingActionButtonColors(),
    primaryButtonSize: Dp = 56.dp
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .background(
                brush = if (showBubble) {
                    colors.outerCircle
                } else SolidColor(Color.Transparent),
                shape = CircleShape
            )
            .padding(10.dp)
            .background(
                brush = if (showBubble) {
                    colors.innerCircle
                } else SolidColor(Color.Transparent),
                shape = CircleShape
            )
            .padding(16.dp)
            .background(
                brush = if (isPressed) {
                    colors.primaryPressed
                } else {
                    colors.primary
                },
                shape = CircleShape
            )
            .size(primaryButtonSize)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

data class BubbleFloatingActionButtonColors(
    val primary: Brush,
    val primaryPressed: Brush,
    val outerCircle: Brush,
    val innerCircle: Brush
)

@Composable
fun rememberBubbleFloatingActionButtonColors(
    primary: Brush = MaterialTheme.colorScheme.buttonGradient,
    primaryPressed: Brush = MaterialTheme.colorScheme.buttonGradientPressed,
    outerCircle: Brush = SolidColor(MaterialTheme.colorScheme.primary95),
    innerCircle: Brush = SolidColor(MaterialTheme.colorScheme.primary90),
): BubbleFloatingActionButtonColors {
    return remember(primary, primaryPressed, outerCircle, innerCircle) {
        BubbleFloatingActionButtonColors(
            primary = primary,
            primaryPressed = primaryPressed,
            outerCircle = outerCircle,
            innerCircle = innerCircle
        )
    }
}