package com.example.esp32_led

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import com.example.esp32_led.ui.screen.HomeScreen
import com.example.esp32_led.ui.theme.SmartHomeTheme
import com.example.esp32_led.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    private val speechLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val text = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()?.lowercase() ?: return@registerForActivityResult
            viewModel.handleVoice(text)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.RECORD_AUDIO), 1
        )

        setContent {
            SmartHomeTheme {
                val uiState by viewModel.uiState.collectAsState()
                val history by viewModel.tempHistory.collectAsState()
                HomeScreen(
                    uiState = uiState,
                    tempHistory = history,
                    onCommand = viewModel::sendCommand,
                    onRefreshSensor = viewModel::fetchSensor,
                    onVoice = ::startVoice,
                    onSnackDismiss = viewModel::clearSnack
                )
            }
        }
    }

    private fun startVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói lệnh của bạn...")
        }
        speechLauncher.launch(intent)
    }
}