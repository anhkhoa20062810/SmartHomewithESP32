package com.example.esp32_led.viewmodel

data class HomeUiState(
    // Sensor
    val temp            : String  = "--",
    val humidity        : String  = "--",
    val isLoadingSensor : Boolean = false,

    // Fan
    val fanOn           : Boolean = false,
    val fanSpeed        : Int     = 0,

    // LEDs
    val led1On          : Boolean = false,
    val led2On          : Boolean = false,

    // Servos
    val servo1Open      : Boolean = false,
    val servo2Open      : Boolean = false,
    val doorOpen        : Boolean = false,

    // Auth
    val isAuthenticated : Boolean = false,
    val authError       : String? = null,

    // Theme
    val isDarkMode      : Boolean = true,

    // UI
    val snackMessage    : String? = null,
    val history         : List<HistoryEntry> = emptyList(),
    val tempAlertEmail  : String? = null  // Khi có giá trị → mở Intent gửi email cảnh báo
)

data class HistoryEntry(
    val id      : Long   = System.currentTimeMillis(),
    val message : String,
    val time    : String,
    val type    : HistoryType
)

enum class HistoryType { COMMAND, SENSOR, ALERT, VOICE, AUTH }

sealed class VoiceCommand {
    object FanOn       : VoiceCommand()
    object FanOff      : VoiceCommand()
    object Led1On      : VoiceCommand()
    object Led1Off     : VoiceCommand()
    object Led2On      : VoiceCommand()
    object Led2Off     : VoiceCommand()
    object DoorOpen    : VoiceCommand()
    object DoorClose   : VoiceCommand()
    object Servo1Open  : VoiceCommand()
    object Servo1Close : VoiceCommand()
    object Servo2Open  : VoiceCommand()
    object Servo2Close : VoiceCommand()
    object Unknown     : VoiceCommand()
}

fun parseVoice(text: String): VoiceCommand = when {
    text.contains("bật quạt")   || text.contains("mở quạt")      -> VoiceCommand.FanOn
    text.contains("tắt quạt")   || text.contains("dừng quạt")    -> VoiceCommand.FanOff
    text.contains("bật đèn 1")  || text.contains("mở đèn 1")     -> VoiceCommand.Led1On
    text.contains("tắt đèn 1")                                    -> VoiceCommand.Led1Off
    text.contains("bật đèn 2")  || text.contains("mở đèn 2")     -> VoiceCommand.Led2On
    text.contains("tắt đèn 2")                                    -> VoiceCommand.Led2Off
    text.contains("bật đèn")    || text.contains("mở đèn")       -> VoiceCommand.Led1On
    text.contains("tắt đèn")                                      -> VoiceCommand.Led1Off
    text.contains("mở cửa")                                       -> VoiceCommand.DoorOpen
    text.contains("đóng cửa")                                     -> VoiceCommand.DoorClose
    text.contains("mở rèm")     || text.contains("mở servo 1")   -> VoiceCommand.Servo1Open
    text.contains("đóng rèm")   || text.contains("đóng servo 1") -> VoiceCommand.Servo1Close
    text.contains("mở cửa sổ")  || text.contains("mở servo 2")   -> VoiceCommand.Servo2Open
    text.contains("đóng cửa sổ")|| text.contains("đóng servo 2") -> VoiceCommand.Servo2Close
    else                                                           -> VoiceCommand.Unknown
}