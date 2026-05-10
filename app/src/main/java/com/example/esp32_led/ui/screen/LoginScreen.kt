package com.example.esp32_led.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    isDarkMode   : Boolean,
    authError    : String?,
    onLogin      : (username: String, password: String) -> Unit,
    onBiometric  : () -> Unit,   // giữ tham số để tránh lỗi call-site, không dùng nữa
    onToggleTheme: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    var username  by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var showPass  by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val gradientBrush = if (isDarkMode) {
        Brush.verticalGradient(listOf(cs.background, cs.surface))
    } else {
        Brush.verticalGradient(listOf(cs.primaryContainer, cs.background))
    }

    Box(
        modifier         = Modifier.fillMaxSize().background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        // Nút đổi theme góc trên phải
        IconButton(
            onClick  = onToggleTheme,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(
                imageVector        = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Toggle theme",
                tint               = cs.primary
            )
        }

        Card(
            modifier  = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            shape     = RoundedCornerShape(28.dp),
            colors    = CardDefaults.cardColors(containerColor = cs.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier            = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Logo
                Box(
                    modifier         = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(cs.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.Home,
                        contentDescription = null,
                        tint               = cs.primary,
                        modifier           = Modifier.size(40.dp)
                    )
                }

                Text("Smart Home", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = cs.onSurface)
                Text("Đăng nhập để điều khiển", fontSize = 13.sp, color = cs.onSurfaceVariant)

                Divider(color = cs.outline.copy(alpha = 0.3f))

                // Username
                OutlinedTextField(
                    value         = username,
                    onValueChange = { username = it },
                    label         = { Text("Tên đăng nhập") },
                    leadingIcon   = { Icon(Icons.Default.Person, null) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(14.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = cs.primary,
                        unfocusedBorderColor = cs.outline
                    )
                )

                // Password
                OutlinedTextField(
                    value                = password,
                    onValueChange        = { password = it },
                    label                = { Text("Mật khẩu") },
                    leadingIcon          = { Icon(Icons.Default.Lock, null) },
                    trailingIcon         = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine           = true,
                    modifier             = Modifier.fillMaxWidth(),
                    shape                = RoundedCornerShape(14.dp),
                    colors               = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = cs.primary,
                        unfocusedBorderColor = cs.outline
                    )
                )

                // Thông báo lỗi
                AnimatedVisibility(visible = authError != null) {
                    Text(text = authError ?: "", color = cs.error, fontSize = 13.sp)
                }

                // Nút đăng nhập
                Button(
                    onClick  = {
                        isLoading = true
                        onLogin(username, password)
                        isLoading = false
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(14.dp),
                    enabled  = username.isNotBlank() && password.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = cs.onPrimary)
                    } else {
                        Icon(Icons.Default.Login, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Đăng nhập", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }

                Text(
                    "Tài khoản mặc định: admin / smarthome123",
                    fontSize = 11.sp,
                    color    = cs.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}