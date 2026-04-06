package com.example.esp32_led.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esp32_led.ui.theme.*

@Composable
fun SensorCard(
    temp      : String,
    humidity  : String,
    isLoading : Boolean,
    onRefresh : () -> Unit,
    modifier  : Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp), // bo tron goc
        colors   = CardDefaults.cardColors(containerColor = DarkSurface),
        border   = BorderStroke(0.5.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("CẢM BIẾN MÔI TRƯỜNG", style = MaterialTheme.typography.labelSmall)
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(16.dp),
                        color       = AccentBlue,
                        strokeWidth = 2.dp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SensorValue(
                    icon  = "🌡️",
                    value = temp,
                    unit  = "°C",
                    label = "Nhiệt độ",
                    valueColor = if (temp != "--" && (temp.toFloatOrNull() ?: 0f) > 35f)
                        RedAlert else TextPrimary
                )
                Divider(
                    modifier  = Modifier.height(60.dp).width(0.5.dp),
                    color     = DarkBorder,
                    thickness = 0.5.dp
                )
                SensorValue(
                    icon       = "💧",
                    value      = humidity,
                    unit       = "%",
                    label      = "Độ ẩm",
                    valueColor = TextPrimary
                )
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick  = onRefresh,
                enabled  = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(30.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = DarkSurface2,
                    contentColor   = AccentBlue
                )
            ) {
                Text("Làm mới dữ liệu", fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun SensorValue(
    icon       : String,
    value      : String,
    unit       : String,
    label      : String,
    valueColor : androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 26.sp)
        Spacer(Modifier.height(6.dp))
        AnimatedContent(
            targetState    = value,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            label          = "val_$label"
        ) { v ->
            Row(verticalAlignment = Alignment.Bottom) {
                Text(v, fontSize = 30.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
                if (v != "--") {
                    Text(unit, fontSize = 13.sp, color = TextSecondary,
                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp))
                }
            }
        }
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}
