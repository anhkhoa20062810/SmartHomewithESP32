package com.example.esp32_led.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.esp32_led.data.RemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RemoteRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _tempHistory = MutableStateFlow<List<Float>>(emptyList())
    val tempHistory: StateFlow<List<Float>> = _tempHistory.asStateFlow()

    private var lastTempAlertTime = 0L

    companion object {
        private const val NOTIF_CHANNEL_ID  = "temp_alert_channel"
        private const val NOTIF_ID_TEMP     = 1001
        private const val TEMP_THRESHOLD    = 40f
        private const val ALERT_COOLDOWN_MS = 5 * 60 * 1000L // 5 phút
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIF_CHANNEL_ID,
                "Cảnh báo nhiệt độ",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo khi nhiệt độ vượt ngưỡng an toàn"
                enableVibration(true)
            }
            val nm = getApplication<Application>()
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    // ── AUTH ──────────────────────────────────────────────────────────

    fun login(username: String, password: String) {
        if (repo.checkLogin(username, password)) {
            _uiState.update { it.copy(isAuthenticated = true, authError = null) }
            addHistory("Đăng nhập: $username", HistoryType.AUTH)
            loadHistory()
            fetchSensor()
        } else {
            _uiState.update { it.copy(authError = "Sai tài khoản hoặc mật khẩu") }
        }
    }

    fun logout() {
        _uiState.update { HomeUiState(isDarkMode = it.isDarkMode) }
    }

    // ── THEME ─────────────────────────────────────────────────────────

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    // ── COMMANDS ──────────────────────────────────────────────────────

    fun sendCommand(cmd: String, label: String) {
        viewModelScope.launch {
            repo.sendCmd(cmd)
                .onSuccess {
                    updateDeviceState(cmd)
                    addHistory(label, HistoryType.COMMAND)
                }
                .onFailure { showSnack("Lỗi gửi lệnh: ${it.message}") }
        }
    }

    fun setFanSpeed(pct: Int) {
        viewModelScope.launch {
            repo.sendCmd("fan_speed", pct)
                .onSuccess {
                    _uiState.update { it.copy(fanSpeed = pct, fanOn = pct > 0) }
                    addHistory("Tốc độ quạt: $pct%", HistoryType.COMMAND)
                }
                .onFailure { showSnack("Lỗi cập nhật tốc độ") }
        }
    }

    private fun updateDeviceState(cmd: String) {
        _uiState.update { s ->
            when (cmd) {
                "fan_on"       -> s.copy(fanOn = true,  fanSpeed = 100)
                "fan_off"      -> s.copy(fanOn = false, fanSpeed = 0)
                "led1_on"      -> s.copy(led1On = true)
                "led1_off"     -> s.copy(led1On = false)
                "led2_on"      -> s.copy(led2On = true)
                "led2_off"     -> s.copy(led2On = false)
                "servo1_open"  -> s.copy(servo1Open = true)
                "servo1_close" -> s.copy(servo1Open = false)
                "servo2_open"  -> s.copy(servo2Open = true)
                "servo2_close" -> s.copy(servo2Open = false)
                "door_open"    -> s.copy(doorOpen = true)
                "door_close"   -> s.copy(doorOpen = false)
                else           -> s
            }
        }
    }

    // ── SENSOR ────────────────────────────────────────────────────────

    fun fetchSensor() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSensor = true) }
            repo.getSensor()
                .onSuccess { (temp, hum) ->
                    _uiState.update { it.copy(temp = temp, humidity = hum, isLoadingSensor = false) }
                    checkTemperatureAlert(temp)
                }
                .onFailure {
                    _uiState.update { it.copy(isLoadingSensor = false) }
                    showSnack("Lỗi đọc cảm biến")
                }
        }
    }

    /** Kiểm tra và gửi cảnh báo khi nhiệt độ > 40°C */
    private fun checkTemperatureAlert(tempStr: String) {
        val temp = tempStr.toFloatOrNull() ?: return
        if (temp > TEMP_THRESHOLD) {
            val now = System.currentTimeMillis()
            if (now - lastTempAlertTime > ALERT_COOLDOWN_MS) {
                lastTempAlertTime = now
                val msg = "⚠️ Nhiệt độ $tempStr°C — vượt ngưỡng an toàn 40°C!"
                showSnack(msg)
                sendTemperatureNotification(temp)
                _uiState.update { it.copy(tempAlertEmail = msg) }
                addHistory("🌡️ CẢNH BÁO: Nhiệt độ $tempStr°C", HistoryType.ALERT)
            }
        }
    }

    /** Đẩy notification lên thanh trạng thái */
    private fun sendTemperatureNotification(temp: Float) {
        try {
            val ctx = getApplication<Application>()
            val notification = NotificationCompat.Builder(ctx, NOTIF_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("⚠️ Cảnh báo nhiệt độ cao!")
                .setContentText("Nhiệt độ hiện tại: ${temp}°C — vượt ngưỡng 40°C")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "Nhiệt độ trong nhà đang ở mức ${temp}°C, vượt ngưỡng an toàn 40°C.\n" +
                                "Vui lòng kiểm tra và điều chỉnh hệ thống làm mát."
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(ctx).notify(NOTIF_ID_TEMP, notification)
        } catch (e: SecurityException) {
            // Chưa cấp quyền POST_NOTIFICATIONS (Android 13+)
        }
    }

    /** Tạo Intent email cảnh báo — MainActivity sẽ gọi startActivity */
    fun buildTempAlertEmailIntent(tempStr: String): android.content.Intent {
        return android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(
                android.content.Intent.EXTRA_SUBJECT,
                "⚠️ [Smart Home] Cảnh báo nhiệt độ cao: ${tempStr}°C"
            )
            putExtra(
                android.content.Intent.EXTRA_TEXT,
                "Kính gửi,\n\nHệ thống Smart Home thông báo:\n\n" +
                        "🌡️ Nhiệt độ hiện tại: ${tempStr}°C\n" +
                        "⚠️ Vượt ngưỡng an toàn: 40°C\n" +
                        "🕐 Thời điểm: ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())}\n\n" +
                        "Vui lòng kiểm tra và xử lý ngay.\n\nTrân trọng,\nHệ thống Smart Home"
            )
        }
    }

    fun clearTempAlertEmail() = _uiState.update { it.copy(tempAlertEmail = null) }

    fun loadHistory() {
        viewModelScope.launch {
            _tempHistory.value = repo.getTempHistory()
        }
    }

    // ── VOICE ─────────────────────────────────────────────────────────

    fun handleVoice(text: String) {
        addHistory("🎤 $text", HistoryType.VOICE)
        val voiceCmd = parseVoice(text.lowercase())
        when (voiceCmd) {
            is VoiceCommand.FanOn       -> sendCommand("fan_on",       "Bật quạt")
            is VoiceCommand.FanOff      -> sendCommand("fan_off",      "Tắt quạt")
            is VoiceCommand.Led1On      -> sendCommand("led1_on",      "Bật đèn 1")
            is VoiceCommand.Led1Off     -> sendCommand("led1_off",     "Tắt đèn 1")
            is VoiceCommand.Led2On      -> sendCommand("led2_on",      "Bật đèn 2")
            is VoiceCommand.Led2Off     -> sendCommand("led2_off",     "Tắt đèn 2")
            is VoiceCommand.DoorOpen    -> sendCommand("door_open",    "Mở cửa")
            is VoiceCommand.DoorClose   -> sendCommand("door_close",   "Đóng cửa")
            is VoiceCommand.Servo1Open  -> sendCommand("servo1_open",  "Mở rèm")
            is VoiceCommand.Servo1Close -> sendCommand("servo1_close", "Đóng rèm")
            is VoiceCommand.Servo2Open  -> sendCommand("servo2_open",  "Mở cửa sổ")
            is VoiceCommand.Servo2Close -> sendCommand("servo2_close", "Đóng cửa sổ")
            else -> showSnack("Không rõ lệnh: $text")
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────

    fun clearSnack() = _uiState.update { it.copy(snackMessage = null) }

    private fun showSnack(msg: String) = _uiState.update { it.copy(snackMessage = msg) }

    private fun addHistory(message: String, type: HistoryType) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val entry = HistoryEntry(message = message, time = time, type = type)
        _uiState.update { s -> s.copy(history = (listOf(entry) + s.history).take(30)) }
    }
}