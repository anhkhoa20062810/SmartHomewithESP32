package com.example.esp32_led.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esp32_led.ui.theme.*

@Composable
fun SoundCard(
    isActive : Boolean,
    modifier : Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue   = if (isActive) RedAlert.copy(alpha = 0.5f) else DarkBorder,
        animationSpec = tween(400),
        label         = "sound_border"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = DarkSurface),
        border   = BorderStroke(0.5.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("PHÁT HIỆN ÂM THANH", style = MaterialTheme.typography.labelSmall)
                SoundStatusPill(isActive)
            }

            Spacer(Modifier.height(14.dp))

            // Animated wave bars
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                repeat(16) { i ->
                    WaveBar(isActive = isActive, delayMs = (i * 60) % 500)
                    Spacer(Modifier.width(3.dp))
                }
            }
        }
    }
}

@Composable
private fun SoundStatusPill(isActive: Boolean) {
    val bg  by animateColorAsState(
        if (isActive) RedAlert.copy(0.15f) else GreenActive.copy(0.12f), tween(400), "pill_bg")
    val fg  by animateColorAsState(
        if (isActive) RedAlert else GreenActive, tween(400), "pill_fg")
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        1f, if (isActive) 0.3f else 1f,
        infiniteRepeatable(tween(600), RepeatMode.Reverse), "dot_pulse"
    )

    Row(
        modifier          = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape)
            .background(fg.copy(alpha = if (isActive) pulse else 1f)))
        Text(
            text     = if (isActive) "Có tiếng!" else "Yên tĩnh",
            fontSize = 12.sp,
            color    = fg
        )
    }
}

@Composable
private fun WaveBar(isActive: Boolean, delayMs: Int) {
    val height by rememberInfiniteTransition(label = "wave").animateFloat(
        initialValue  = 5f,
        targetValue   = if (isActive) 28f else 7f,
        animationSpec = infiniteRepeatable(
            tween(if (isActive) 380 else 1400, delayMillis = delayMs, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "bar_h"
    )
    val color by animateColorAsState(
        if (isActive) AccentBlue else DarkBorder, tween(400), "bar_c")

    Box(
        Modifier.width(4.dp).height(height.dp)
            .clip(RoundedCornerShape(2.dp)).background(color)
    )
}
