package com.example.esp32_led.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.esp32_led.ui.theme.*

@Composable
fun IpDialog(
    currentIp : String,
    onConfirm : (String) -> Unit,
    onDismiss : () -> Unit
) {
    var input by remember { mutableStateOf(currentIp) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = DarkSurface,
        shape            = RoundedCornerShape(20.dp),
        title  = { Text("Địa chỉ thiết bị", color = TextPrimary) },
        text   = {
            Column {
                Text(
                    "Nhập IP của ESP32/ESP8266:",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value           = input,
                    onValueChange   = { input = it },
                    singleLine      = true,
                    placeholder     = { Text("192.168.x.x", color = TextHint) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction    = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { if (input.isNotBlank()) onConfirm(input.trim()) }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AccentBlue,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor     = TextPrimary,
                        unfocusedTextColor   = TextPrimary,
                        cursorColor          = AccentBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (input.isNotBlank()) onConfirm(input.trim()) }) {
                Text("Lưu", color = AccentBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = TextMuted)
            }
        }
    )
}
