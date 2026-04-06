package com.example.esp32_led.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esp32_led.ui.components.*
import com.example.esp32_led.ui.theme.*
import com.example.esp32_led.viewmodel.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tempHistory: List<Float>,
    uiState         : HomeUiState,
    onCommand       : (cmd: String, label: String) -> Unit,
    onRefreshSensor : () -> Unit,
    onVoice         : () -> Unit,
    onSnackDismiss  : () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackMessage) {
        uiState.snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            onSnackDismiss()
        }
    }

    Scaffold(
        containerColor = DarkBg,
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Smart Home",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary
                        )
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            OnlineDot()
                            Text("Firebase Online", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            SensorCard(
                temp      = uiState.temp,
                humidity  = uiState.humidity,
                isLoading = uiState.isLoadingSensor,
                onRefresh = onRefreshSensor


            )
            TemperatureChart(data = tempHistory)

            SoundCard(isActive = uiState.soundActive)

            ControlPanel(
                fanOn    = uiState.fanOn,
                ledOn    = uiState.ledOn,
                doorOpen = uiState.doorOpen,
                onCommand = onCommand
            )

            VoiceButton(onClick = onVoice)

            HistoryList(history = uiState.history)

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OnlineDot() {
    val alpha by rememberInfiniteTransition(label = "dot").animateFloat(
        1f, 0.3f,
        infiniteRepeatable(tween(1200), RepeatMode.Reverse), "dot_a"
    )
    Box(
        Modifier.size(6.dp)
            .clip(CircleShape)
            .background(GreenActive.copy(alpha = alpha))
    )
}