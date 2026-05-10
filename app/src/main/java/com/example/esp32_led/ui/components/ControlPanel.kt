package com.example.esp32_led.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── DATA CLASS ───────────────────────────────────────────────────────

private data class DeviceItem(
    val label: String,
    val emoji: String,
    val isOn: Boolean,
    val cmdOn: String,
    val cmdOff: String,
    val labelOn: String = "Bật",
    val labelOff: String = "Tắt",
    val colorOn: Color = Color(0xFF00E676)
)

// ── MAIN CONTROL PANEL ───────────────────────────────────────────────

@Composable
fun ControlPanel(
    fanOn: Boolean,
    fanSpeed: Int,
    led1On: Boolean,
    led2On: Boolean,
    doorOpen: Boolean,
    servo1Open: Boolean,
    servo2Open: Boolean,
    onCommand: (cmd: String, label: String) -> Unit,
    onFanSpeed: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, cs.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "ĐIỀU KHIỂN THIẾT BỊ",
                style = MaterialTheme.typography.labelLarge,
                color = cs.primary,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            // ── ROW 1: Fan + LED1 + LED2 ─────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val devices = listOf(
                    DeviceItem("Quạt", "🌀", fanOn, "fan_on", "fan_off", colorOn = Color(0xFF2979FF)),
                    DeviceItem("Đèn 1", "💡", led1On, "led1_on", "led1_off", colorOn = Color(0xFFFFAB00)),
                    DeviceItem("Đèn 2", "🌟", led2On, "led2_on", "led2_off", colorOn = Color(0xFFFFD740))
                )

                devices.forEach { device ->
                    DeviceButton(
                        device = device,
                        onToggle = {
                            val cmd = if (device.isOn) device.cmdOff else device.cmdOn
                            val msg = if (device.isOn) "${device.labelOff} ${device.label}" else "${device.labelOn} ${device.label}"
                            onCommand(cmd, msg)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── FAN SPEED SLIDER ──────────────────────────────────────
            AnimatedVisibility(
                visible = fanOn,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FanSpeedSlider(speed = fanSpeed, onSpeedChange = onFanSpeed)
            }

            // ── ROW 2: Servo1 + Servo2 + Door ────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val devices = listOf(
                    DeviceItem("Rèm", "🪟", servo1Open, "servo1_open", "servo1_close", "Mở", "Đóng", Color(0xFF00BCD4)),
                    DeviceItem("Cửa sổ", "🔲", servo2Open, "servo2_open", "servo2_close", "Mở", "Đóng", Color(0xFF8BC34A)),
                    DeviceItem("Cửa", "🚪", doorOpen, "door_open", "door_close", "Mở", "Đóng", Color(0xFF00E676))
                )

                devices.forEach { device ->
                    DeviceButton(
                        device = device,
                        onToggle = {
                            val cmd = if (device.isOn) device.cmdOff else device.cmdOn
                            val msg = if (device.isOn) "${device.labelOff} ${device.label}" else "${device.labelOn} ${device.label}"
                            onCommand(cmd, msg)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ── FAN SPEED SLIDER ─────────────────────────────────────────────────

@Composable
private fun FanSpeedSlider(speed: Int, onSpeedChange: (Int) -> Unit) {
    val cs = MaterialTheme.colorScheme
    var sliderPos by remember { mutableStateOf(speed.toFloat()) }

    LaunchedEffect(speed) { sliderPos = speed.toFloat() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cs.secondaryContainer.copy(alpha = 0.3f))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🌀 Tốc độ quạt", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(
                "${sliderPos.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2979FF),
                fontWeight = FontWeight.Black
            )
        }
        Slider(
            value = sliderPos,
            onValueChange = { sliderPos = it },
            onValueChangeFinished = { onSpeedChange(sliderPos.toInt()) },
            valueRange = 0f..100f,
            steps = 3, // 0, 25, 50, 75, 100
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF2979FF),
                activeTrackColor = Color(0xFF2979FF),
                inactiveTrackColor = cs.outlineVariant
            )
        )
    }
}

// ── DEVICE BUTTON ────────────────────────────────────────────────────

@Composable
private fun DeviceButton(
    device: DeviceItem,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "button_scale"
    )

    val containerColor by animateColorAsState(
        if (device.isOn) device.colorOn.copy(alpha = 0.12f) else cs.surfaceVariant.copy(alpha = 0.4f),
        label = "bg_color"
    )

    val contentColor by animateColorAsState(
        if (device.isOn) device.colorOn else cs.onSurfaceVariant,
        label = "content_color"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .border(
                width = 1.dp,
                color = if (device.isOn) device.colorOn.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Tắt hiệu ứng ripple mặc định để dùng scale cho hiện đại
                onClick = onToggle
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(device.emoji, fontSize = 28.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            device.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            if (device.isOn) device.labelOn else device.labelOff,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.ExtraBold
        )
    }
}