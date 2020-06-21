package com.example.playground

class Response(
    val runningTimes: List<RunningTime>,
    val summary: Summary
)

data class RunningTime(
    val runningTimeInSeconds : Int,
    val startTime : String,
    val endTime : String
)

data class Summary(
    val startTime: String,
    val endTime: String,
    val meanInSecondsPerDay: Int,
    val totalRunningTimeInSeconds: Int
)
