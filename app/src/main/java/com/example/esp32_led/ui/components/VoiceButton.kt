package com.example.esp32_led.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esp32_led.ui.theme.*

@Composable
fun VoiceButton(
    onClick  : () -> Unit,
    modifier : Modifier = Modifier
) {
    val source    = remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "vscale"
    )

    val glowAlpha by rememberInfiniteTransition(label = "glow").animateFloat(
        0.3f, 0.7f,
        infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        "glow_a"
    )

    Box(
        modifier         = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(AccentBlue)
            .border(1.dp, AccentBlueLight.copy(alpha = glowAlpha), RoundedCornerShape(16.dp))
            .clickable(source, null, onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("🎤", fontSize = 18.sp)
            Text(
                text       = "Ra lệnh bằng giọng nói",
                fontSize   = 15.sp,
                fontWeight = FontWeight.Medium,
                color      = TextPrimary
            )
        }
    }
}
