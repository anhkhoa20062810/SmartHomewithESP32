package com.example.esp32_led

import retrofit2.http.GET

interface EspService {
    @GET("on")
    suspend fun turnOn(): String

    @GET("off")
    suspend fun turnOff(): String
}