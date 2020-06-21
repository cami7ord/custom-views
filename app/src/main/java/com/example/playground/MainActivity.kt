package com.example.playground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.widgets.charts.BarGraphData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = listOf<RunningTime>(
            RunningTime(83846, "2020-05-20 00:00:00", "2020-05-21 00:00:00"),
            RunningTime(66548, "2020-05-21 00:00:00", "2020-05-22 00:00:00"),
            RunningTime(81831, "2020-05-22 00:00:00", "2020-05-23 00:00:00"),
            RunningTime(76837, "2020-05-23 00:00:00", "2020-05-24 00:00:00"),
            RunningTime(9793, "2020-05-24 00:00:00", "2020-05-25 00:00:00"),
            RunningTime(71826, "2020-05-25 00:00:00", "2020-05-26 00:00:00"),
            RunningTime(61046, "2020-05-26 00:00:00", "2020-05-27 00:00:00"),
            RunningTime(44012, "2020-05-27 00:00:00", "2020-05-28 00:00:00"),
            RunningTime(1958, "2020-05-28 00:00:00", "2020-05-29 00:00:00"),
            RunningTime(20496, "2020-05-29 00:00:00", "2020-05-30 00:00:00"),
            RunningTime(420, "2020-06-06 00:00:00", "2020-06-07 00:00:00"),
            RunningTime(450, "2020-06-07 00:00:00", "2020-06-08 00:00:00"),
            RunningTime(500, "2020-06-08 00:00:00", "2020-06-09 00:00:00")
        ).map {
            BarGraphData.DataSetEntry<RunningTime, Int> (it, it.runningTimeInSeconds)
        }

        val summary = Summary(
            "2020-05-20 00:00:00",
            "2020-06-09 00:00:00",
            39966,
            519563
        )

        val guides = listOf(
            summary
        )

        chart.props = BarGraphData<RunningTime, Int>().apply {
            data = Data(
                dataSet = list,
                yGuides = guides.map {
                    YGuide(it.meanInSecondsPerDay, "${it.meanInSecondsPerDay}h")
                }
            )
        }
    }
}
