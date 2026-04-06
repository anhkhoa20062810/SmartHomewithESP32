package com.example.esp32_led.viewmodel

data class HomeUiState(
    val temp            : String  = "--",
    val humidity        : String  = "--",
    val soundActive     : Boolean = false,
    val fanOn           : Boolean = false,
    val ledOn           : Boolean = false,
    val doorOpen        : Boolean = false,
    val isLoadingSensor : Boolean = false,
    val snackMessage    : String? = null,
    val history         : List<HistoryEntry> = emptyList()
)

data class HistoryEntry(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val time: String,
    val type: HistoryType
)

enum class HistoryType { COMMAND, SENSOR, ALERT, VOICE }

sealed class VoiceCommand {
    object FanOn     : VoiceCommand()
    object FanOff    : VoiceCommand()
    object LedOn     : VoiceCommand()
    object LedOff    : VoiceCommand()
    object DoorOpen  : VoiceCommand()
    object DoorClose : VoiceCommand()
    object Unknown   : VoiceCommand()
}

fun parseVoice(text: String): VoiceCommand = when {
    text.contains("bật quạt") || text.contains("mở quạt")  -> VoiceCommand.FanOn
    text.contains("tắt quạt") || text.contains("đóng quạt") -> VoiceCommand.FanOff
    text.contains("bật đèn")  || text.contains("mở đèn")   -> VoiceCommand.LedOn
    text.contains("tắt đèn")  || text.contains("đóng đèn") -> VoiceCommand.LedOff
    text.contains("mở cửa")                                 -> VoiceCommand.DoorOpen
    text.contains("đóng cửa")                               -> VoiceCommand.DoorClose
    else                                                     -> VoiceCommand.Unknown
}
