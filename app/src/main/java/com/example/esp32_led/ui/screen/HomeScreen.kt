package com.example.esp32_led.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esp32_led.ui.components.*
import com.example.esp32_led.viewmodel.HomeUiState
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tempHistory     : List<Float>,
    uiState         : HomeUiState,
    onCommand       : (cmd: String, label: String) -> Unit,
    onFanSpeed      : (Int) -> Unit,
    onRefreshSensor : () -> Unit,
    onVoice         : () -> Unit,
    onSnackDismiss  : () -> Unit,
    onToggleTheme   : () -> Unit,
    onLogout        : () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.snackMessage) {
        uiState.snackMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            onSnackDismiss()
        }
    }

    Scaffold(
        containerColor = cs.background,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData   = data,
                    containerColor = cs.secondaryContainer,
                    contentColor   = cs.onSecondaryContainer,
                    shape          = CircleShape
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "SMART HOME",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = cs.onSurface
                        )
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OnlineDot()
                            Text(
                                "Hệ thống trực tuyến",
                                style = MaterialTheme.typography.labelSmall,
                                color = cs.primary.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector        = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme",
                            tint               = cs.primary
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, "Logout", tint = cs.error.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = cs.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(cs.background)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoadingSensor && uiState.temp == "--") {
                LinearProgressIndicator(
                    modifier   = Modifier.fillMaxWidth().clip(CircleShape),
                    color      = cs.primary,
                    trackColor = cs.primaryContainer
                )
            }

            // 1. Thẻ cảm biến nhiệt độ & độ ẩm
            SensorCard(
                temp      = uiState.temp,
                humidity  = uiState.humidity,
                isLoading = uiState.isLoadingSensor,
                onRefresh = onRefreshSensor
            )

            // 2. Biểu đồ nhiệt độ
            if (tempHistory.isNotEmpty()) {
                TemperatureChart(data = tempHistory)
            }

            // 3. Bảng điều khiển thiết bị
            ControlPanel(
                fanOn      = uiState.fanOn,
                fanSpeed   = uiState.fanSpeed,
                led1On     = uiState.led1On,
                led2On     = uiState.led2On,
                doorOpen   = uiState.doorOpen,
                servo1Open = uiState.servo1Open,
                servo2Open = uiState.servo2Open,
                onCommand  = onCommand,
                onFanSpeed = onFanSpeed
            )

            // 4. Nút giọng nói
            VoiceButton(onClick = onVoice)

            // 5. Lịch sử hoạt động
            Text(
                "LỊCH SỬ HOẠT ĐỘNG",
                style      = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color      = cs.onBackground.copy(alpha = 0.6f),
                modifier   = Modifier.padding(top = 8.dp)
            )
            HistoryList(history = uiState.history)

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun OnlineDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 0.3f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color(0xFF00E676).copy(alpha = alpha))
    )
}