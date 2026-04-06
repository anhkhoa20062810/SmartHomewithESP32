package com.example.esp32_led.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.esp32_led.data.RemoteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RemoteRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var soundPollingJob: Job? = null

    private val _tempHistory = MutableStateFlow<List<Float>>(emptyList())
    val tempHistory: StateFlow<List<Float>> = _tempHistory

    init {
        startSoundPolling()
        //startSensorRealtime()
        loadHistory() // 🔥 thêm dòng này
    }

    // ── COMMAND (FIREBASE) ───────────────────────

    fun sendCommand(cmd: String, label: String) {
        viewModelScope.launch {
            try {
                repo.sendCmd(cmd)
                    .onSuccess {
                        updateDeviceState(cmd)
                        addHistory(label, HistoryType.COMMAND)
                    }
                    .onFailure {
                        showSnack("Lỗi Firebase: ${it.message}")
                    }
            } catch (e: Exception) {
                showSnack("Lỗi mạng")
            }
        }
    }

    private fun updateDeviceState(cmd: String) {
        _uiState.update { s ->
            when (cmd) {

                "fan_on" -> s.copy(fanOn = true)
                "fan_off" -> s.copy(fanOn = false)

                "led_on", "led_all_on" -> s.copy(ledOn = true)
                "led_off", "led_all_off" -> s.copy(ledOn = false)

                "door_open" -> s.copy(doorOpen = true)
                "door_close" -> s.copy(doorOpen = false)

                else -> s
            }
        }
    }
    fun loadHistory() {
        viewModelScope.launch {
            val snapshot = repo.database.getReference("history").get().await()

            val list = mutableListOf<Float>()

            snapshot.children.forEach {
                val value = it.value.toString().toFloatOrNull()
                if (value != null) list.add(value)
            }

            _tempHistory.value = list.takeLast(10) // lấy 10 điểm gần nhất
        }
    }

    // ── SENSOR (FIREBASE) ───────────────────────

    fun fetchSensor() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSensor = true) }

            try {
                repo.getSensor()
                    .onSuccess { (temp, hum) ->
                        _uiState.update {
                            it.copy(
                                temp = temp,
                                humidity = hum,
                                isLoadingSensor = false
                            )
                        }
                        addHistory("Nhiệt độ: ${temp}°C  Độ ẩm: ${hum}%", HistoryType.SENSOR)
                    }
                    .onFailure {
                        _uiState.update { it.copy(isLoadingSensor = false) }
                        showSnack("Không đọc được cảm biến")
                    }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingSensor = false) }
                showSnack("Lỗi mạng")
            }
        }
    }

    // ── SOUND (FIREBASE) ────────────────────────

    private fun startSoundPolling() {
        soundPollingJob?.cancel()

        soundPollingJob = viewModelScope.launch {
            while (true) {
                try {
                    val active = repo.getSound()

                    val wasOff = !_uiState.value.soundActive

                    _uiState.update { it.copy(soundActive = active) }

                    if (active && wasOff) {
                        addHistory("Phát hiện âm thanh!", HistoryType.ALERT)
                    }

                } catch (e: Exception) {
                    // tránh crash
                }

                delay(1000)
            }
        }
    }

    // ── VOICE ───────────────────────────────────

    fun handleVoice(text: String) {
        addHistory("🎤 \"$text\"", HistoryType.VOICE)

        when (parseVoice(text.lowercase())) {
            VoiceCommand.FanOn     -> sendCommand("fan_on", "Bật quạt")
            VoiceCommand.FanOff    -> sendCommand("fan_off", "Tắt quạt")
            VoiceCommand.LedOn     -> sendCommand("led_on", "Bật đèn")
            VoiceCommand.LedOff    -> sendCommand("led_off", "Tắt đèn")
            VoiceCommand.DoorOpen  -> sendCommand("door_open", "Mở cửa")
            VoiceCommand.DoorClose -> sendCommand("door_close", "Đóng cửa")
            VoiceCommand.Unknown   -> showSnack("Không nhận diện: \"$text\"")
        }
    }

    // ── HELPERS ─────────────────────────────────

    fun clearSnack() {
        _uiState.update { it.copy(snackMessage = null) }
    }

    private fun showSnack(msg: String) {
        _uiState.update { it.copy(snackMessage = msg) }
    }

    private fun addHistory(message: String, type: HistoryType) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val entry = HistoryEntry(
            message = message,
            time = time,
            type = type
        )

        _uiState.update { s ->
            s.copy(history = (listOf(entry) + s.history).take(30))
        }
    }
}