package com.example.esp32_led.ui.components

import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*

@Composable
fun TemperatureChart(data: List<Float>) {

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    500
                )
            }
        },
        update = { chart ->

            val entries = data.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }

            val dataSet = LineDataSet(entries, "Temperature").apply {
                color = Color.RED
                valueTextColor = Color.WHITE
                lineWidth = 2f
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}