package com.example.playground

import com.example.widgets.charts.BarChartData.Labelizable

class Response(
    val runningTimes: List<RunningTime>,
    val summary: Summary
)

data class RunningTime(
    val runningTimeInSeconds : Int,
    val startTime : String,
    val endTime : String
): Labelizable {
    override fun labelize(): String? {
        return startTime.substring(8, 10)
    }
}

data class Summary(
    val startTime: String,
    val endTime: String,
    val meanInSecondsPerDay: Int,
    val totalRunningTimeInSeconds: Int
)
