package com.example.esp32_led

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import com.example.esp32_led.ui.screen.HomeScreen
import com.example.esp32_led.ui.screen.LoginScreen
import com.example.esp32_led.ui.theme.SmartHomeTheme
import com.example.esp32_led.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    private val speechLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()?.lowercase()
            text?.let { viewModel.handleVoice(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Xin quyền mic cho giọng nói
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)

        // Xin quyền thông báo (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 2
            )
        }

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val history by viewModel.tempHistory.collectAsState()

            // Khi có cảnh báo nhiệt độ → mở ứng dụng email
            LaunchedEffect(uiState.tempAlertEmail) {
                uiState.tempAlertEmail?.let {
                    val emailIntent = viewModel.buildTempAlertEmailIntent(uiState.temp)
                    startActivity(Intent.createChooser(emailIntent, "Gửi cảnh báo qua email"))
                    viewModel.clearTempAlertEmail()
                }
            }

            SmartHomeTheme(darkMode = uiState.isDarkMode) {
                AnimatedContent(
                    targetState = uiState.isAuthenticated,
                    transitionSpec = {
                        (fadeIn(tween(400)) + slideInVertically { it / 10 }) togetherWith fadeOut(tween(200))
                    },
                    label = "auth_transition"
                ) { isAuthenticated ->
                    if (isAuthenticated) {
                        HomeScreen(
                            uiState = uiState,
                            tempHistory = history,
                            onCommand = viewModel::sendCommand,
                            onFanSpeed = viewModel::setFanSpeed,
                            onRefreshSensor = viewModel::fetchSensor,
                            onVoice = ::startVoice,
                            onSnackDismiss = viewModel::clearSnack,
                            onToggleTheme = viewModel::toggleTheme,
                            onLogout = viewModel::logout
                        )
                    } else {
                        LoginScreen(
                            isDarkMode = uiState.isDarkMode,
                            authError = uiState.authError,
                            onLogin = viewModel::login,
                            onBiometric = { },
                            onToggleTheme = viewModel::toggleTheme
                        )
                    }
                }
            }
        }
    }

    private fun startVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói lệnh...")
        }
        speechLauncher.launch(intent)
    }
}