package com.raulastete.aura.core.presentation.util.modifier

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.defaultShadow(shape: Shape = CircleShape): Modifier {
    return this.shadow(
        elevation = 4.dp,
        shape = shape,
        ambientColor = DefaultShadowColor.copy(0.3f),
        spotColor = DefaultShadowColor.copy(0.3f)
    )
}