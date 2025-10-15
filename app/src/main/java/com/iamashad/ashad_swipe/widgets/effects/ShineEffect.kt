package com.iamashad.ashad_swipe.widgets.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun ShineEffect(
    modifier: Modifier = Modifier,
    trigger: Boolean,
    durationMillis: Int = 1000
) {
    if (!trigger) return

    val infiniteTransition = rememberInfiniteTransition(label = "ShineEffect")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShineX"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val gradientWidth = width * 0.25f
        val startX = offsetX * width

        val start = Offset(startX, 0f)
        val end = Offset(startX + gradientWidth, height)

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.25f),
                    Color.Transparent
                ),
                start = start,
                end = end
            ),
            blendMode = BlendMode.Lighten
        )
    }
}
