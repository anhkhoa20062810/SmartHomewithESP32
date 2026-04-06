package com.example.esp32_led.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.esp32_led.viewmodel.HistoryEntry

@Composable
fun HistoryList(history: List<HistoryEntry>) {

    LazyColumn(
        modifier = Modifier.height(250.dp)
    ) {
        items(
            items = history,
            key = { it.id } // 🔥 FIX
        ) { entry ->

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                Text("${entry.time} - ${entry.message}")
            }
        }
    }
}