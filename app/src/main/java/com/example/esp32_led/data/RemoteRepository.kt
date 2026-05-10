package com.example.esp32_led.data

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RemoteRepository {

    // Khởi tạo Database instance một lần duy nhất
    private val database by lazy {
        FirebaseDatabase.getInstance("https://smarthome-954f9-default-rtdb.firebaseio.com/")
    }

    // ── SEND COMMAND ─────────────────────────────────────────────────
    suspend fun sendCmd(cmd: String, value: Any = ""): Result<Unit> {
        return try {
            val (path, finalValue) = when (cmd) {
                // LED điều khiển qua chân D4, D5
                "led1_on" -> "led1" to "on"
                "led1_off" -> "led1" to "off"
                "led2_on" -> "led2" to "on"
                "led2_off" -> "led2" to "off"

                // QUẠT điều khiển qua chân D18
                "fan_on" -> "fan" to "on"
                "fan_off" -> "fan" to "off"
                "fan_auto" -> "fan" to "auto"
                "fan_speed" -> "fan_speed_set" to value

                // SERVO điều khiển Rèm (D19) và Cửa sổ (D21)
                "servo1_open" -> "servo1" to "open"
                "servo1_close" -> "servo1" to "close"
                "servo2_open" -> "servo2" to "open"
                "servo2_close" -> "servo2" to "close"

                // CỬA CHÍNH (D22)
                "door_open" -> "door" to "open"
                "door_close" -> "door" to "close"

                // Ghi chú: Đã lược bỏ AUTH_RESULT vì bạn không dùng vân tay nữa
                else -> throw Exception("Lệnh không hợp lệ: $cmd")
            }

            database.getReference(path).setValue(finalValue).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── SENSOR ───────────────────────────────────────────────────────
    suspend fun getSensor(): Result<Pair<String, String>> {
        return try {
            // Lấy dữ liệu nhiệt độ và độ ẩm từ DHT11
            val tempSnap = database.getReference("temp").get().await()
            val humSnap = database.getReference("humidity").get().await()

            val temp = tempSnap.value?.toString() ?: "--"
            val hum = humSnap.value?.toString() ?: "--"

            Result.success(temp to hum)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── SOUND ────────────────────────────────────────────────────────
    suspend fun getSound(): Boolean {
        return try {
            // Kiểm tra trạng thái cảm biến âm thanh
            val snap = database.getReference("sound").get().await()
            snap.value?.toString() == "1"
        } catch (e: Exception) {
            false
        }
    }

    // ── TEMP HISTORY (FIX OOM) ───────────────────────────────────────
    suspend fun getTempHistory(): List<Float> {
        return try {
            // Chỉ lấy 20 giá trị gần nhất để vẽ biểu đồ, tránh treo app
            val snap = database.getReference("history")
                .limitToLast(20)
                .get()
                .await()

            val list = mutableListOf<Float>()
            snap.children.forEach { child ->
                child.value?.toString()?.toFloatOrNull()?.let { list.add(it) }
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── LOGIN ────────────────────────────────────────────────────────
    fun checkLogin(username: String, password: String): Boolean {
        // Tài khoản mặc định cho đồ án
        return username == "admin" && password == "smarthome123"
    }
}