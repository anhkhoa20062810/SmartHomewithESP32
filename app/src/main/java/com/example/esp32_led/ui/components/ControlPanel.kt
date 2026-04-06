package com.example.esp32_led.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esp32_led.ui.theme.*

private data class DeviceItem(
    val label    : String,
    val icon     : String,
    val isOn     : Boolean,
    val cmdOn    : String,
    val cmdOff   : String,
    val labelOn  : String = "Bật",
    val labelOff : String = "Tắt",
    val colorOn  : Color  = GreenActive
)

@Composable
fun ControlPanel(
    fanOn     : Boolean,
    ledOn     : Boolean,
    doorOpen  : Boolean,
    onCommand : (cmd: String, label: String) -> Unit,
    modifier  : Modifier = Modifier
) {
    val devices = listOf(
        DeviceItem("Quạt", "🌀", fanOn,   "fan_on",    "fan_off",   colorOn = AccentBlue),
        DeviceItem("Đèn",  "💡", ledOn,   "led_all_on","led_all_off",colorOn = AmberWarn),
        DeviceItem("Cửa",  "🚪", doorOpen,"door_open", "door_close",
            labelOn = "Mở", labelOff = "Đóng", colorOn = GreenActive)
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = DarkSurface),
        border   = BorderStroke(0.5.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ĐIỀU KHIỂN THIẾT BỊ", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(14.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                devices.forEach { device ->
                    DeviceButton(
                        device   = device,
                        onToggle = {
                            if (device.isOn)
                                onCommand(device.cmdOff, "${device.labelOff} ${device.label}")
                            else
                                onCommand(device.cmdOn,  "${device.labelOn} ${device.label}")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceButton(
    device   : DeviceItem,
    onToggle : () -> Unit,
    modifier : Modifier = Modifier
) {
    val source    = remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val scale     by animateFloatAsState(if (isPressed) 0.93f else 1f,
        spring(stiffness = Spring.StiffnessMedium), label = "scale")
    val bg        by animateColorAsState(
        if (device.isOn) device.colorOn.copy(0.13f) else DarkSurface2, tween(300), "bg")
    val border    by animateColorAsState(
        if (device.isOn) device.colorOn.copy(0.5f) else DarkBorder, tween(300), "border")
    val stateColor by animateColorAsState(
        if (device.isOn) device.colorOn else TextMuted, tween(300), "state")

    Column(
        modifier            = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(0.5.dp, border, RoundedCornerShape(16.dp))
            .clickable(source, null, onClick = onToggle)
            .padding(vertical = 16.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(device.icon, fontSize = 26.sp)
        Spacer(Modifier.height(8.dp))
        Text(device.label, fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(
            text       = if (device.isOn) device.labelOn else device.labelOff,
            fontSize   = 11.sp,
            color      = stateColor,
            fontWeight = FontWeight.Medium
        )
    }
}
