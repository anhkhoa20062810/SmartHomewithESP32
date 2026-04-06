package com.example.esp32_led.data

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RemoteRepository {

    // ===== FIREBASE =====
    val database = FirebaseDatabase.getInstance(
        "https://smarthome-954f9-default-rtdb.firebaseio.com/"
    )

    // ── SEND COMMAND ─────────────────────────────
    suspend fun sendCmd(cmd: String): Result<Unit> {
        return try {
            when (cmd) {

                // LED
                "led_on", "led_all_on" -> database.getReference("led").setValue("on").await()
                "led_off", "led_all_off" -> database.getReference("led").setValue("off").await()

                // FAN
                "fan_on" -> database.getReference("fan").setValue("on").await()
                "fan_off" -> database.getReference("fan").setValue("off").await()
                "fan_auto" -> database.getReference("fan").setValue("auto").await()

                // DOOR
                "door_open" -> database.getReference("door").setValue("open").await()
                "door_close" -> database.getReference("door").setValue("close").await()

                else -> throw Exception("Unknown command: $cmd") // debug rõ hơn
            }

            Result.success(Unit)

        } catch (e: Exception) {
            e.printStackTrace() // 🔥 in lỗi thật
            Result.failure(e)
        }
    }

    // ── SENSOR (ĐỌC FIREBASE) ───────────────────
    suspend fun getSensor(): Result<Pair<String, String>> {
        return try {
            val tempSnapshot = database.getReference("temp").get().await()
            val humSnapshot = database.getReference("humidity").get().await()

            val temp = tempSnapshot.value.toString()
            val hum = humSnapshot.value.toString()

            Result.success(Pair(temp, hum))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── SOUND (NẾU BẠN CÓ) ──────────────────────
    suspend fun getSound(): Boolean {
        return try {
            val snapshot = database.getReference("sound").get().await()
            snapshot.value.toString() == "1"
        } catch (e: Exception) {
            false
        }
    }
}