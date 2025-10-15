package com.iamashad.ashad_swipe.widgets.badges

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PendingBadge(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(999.dp)

    // Pulsing dot animation
    val infinite = rememberInfiniteTransition(label = "pending-dot")
    val scale by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pending-scale"
    )
    val alpha by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pending-alpha"
    )

    val c = MaterialTheme.colorScheme
    val grad = Brush.horizontalGradient(
        listOf(
            c.secondaryContainer.copy(alpha = 0.85f),
            c.tertiaryContainer.copy(alpha = 0.85f)
        )
    )

    Surface(
        modifier = modifier,
        shape = shape,
        tonalElevation = 2.dp,
        shadowElevation = 6.dp,
        color = Color.Transparent,
        border = BorderStroke(1.dp, c.outline.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .background(grad, shape)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulsing dot
            Box(
                modifier = Modifier
                    .size((7.dp * scale).coerceAtLeast(6.dp))
                    .clip(CircleShape)
                    .background(c.onSecondaryContainer.copy(alpha = alpha))
            )

            Spacer(Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Rounded.CloudUpload,
                contentDescription = null,
                tint = c.onSecondaryContainer.copy(alpha = 0.95f),
                modifier = Modifier.size(16.dp)
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = "Pending",
                color = c.onSecondaryContainer,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
