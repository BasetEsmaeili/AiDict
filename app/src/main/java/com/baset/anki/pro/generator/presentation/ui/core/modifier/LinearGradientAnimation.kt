package com.baset.anki.pro.generator.presentation.ui.core.modifier

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.animatedGradient(
    primaryColor: Color,
    containerColor: Color,
    cornerRadius: CornerRadius = CornerRadius.Zero
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "LinearGradientInfiniteTransition")
    val colors = listOf(
        primaryColor,
        containerColor,
        primaryColor
    )
    var size by remember { mutableStateOf(Size.Zero) }
    val offsetXAnimation by transition.animateFloat(
        initialValue = -size.width,
        targetValue = size.width,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "LinearGradientXOffsetAnimation"
    )
    drawBehind {
        size = this.size
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(
                    x = offsetXAnimation,
                    y = 0f
                ),
                end = Offset(
                    x = offsetXAnimation + size.width,
                    y = size.height
                )
            ),
            cornerRadius = cornerRadius
        )
    }
}