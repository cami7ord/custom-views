package com.example.playground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.widgets.charts.BarChartData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = listOf<RunningTime>(
            RunningTime(83846, "2020-05-20 00:00:00", "2020-05-21 00:00:00"),
            RunningTime(66548, "2020-05-21 00:00:00", "2020-05-22 00:00:00"),
            RunningTime(81831, "2020-05-22 00:00:00", "2020-05-23 00:00:00"),
            RunningTime(0, "2020-05-23 00:00:00", "2020-05-24 00:00:00"),
            RunningTime(9793, "2020-05-24 00:00:00", "2020-05-25 00:00:00"),
            RunningTime(71826, "2020-05-25 00:00:00", "2020-05-26 00:00:00"),
            RunningTime(61046, "2020-05-26 00:00:00", "2020-05-27 00:00:00"),
            RunningTime(0, "2020-05-27 00:00:00", "2020-05-28 00:00:00"),
            RunningTime(1958, "2020-05-28 00:00:00", "2020-05-29 00:00:00"),
            RunningTime(20496, "2020-05-29 00:00:00", "2020-05-30 00:00:00"),
            RunningTime(4200, "2020-06-06 00:00:00", "2020-06-07 00:00:00")/*,
            RunningTime(450, "2020-06-07 00:00:00", "2020-06-08 00:00:00"),
            RunningTime(500, "2020-06-08 00:00:00", "2020-06-09 00:00:00")*/
        ).map {
            BarChartData.DataSetEntry<RunningTime, Int> (it, it.runningTimeInSeconds)
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

        chart.props = BarChartData<RunningTime, Int>().apply {

            style = Style(
                yAxis = YAxis(0, 86400), // seconds in day
                yGridLines = listOf(
                    YGridLine(24 * 3600, "24h"),
                    YGridLine(16 * 3600, "16h"),
                    YGridLine(8 * 3600, "8h"),
                    YGridLine(0 * 3600, "0h")
                ),
                xGridValues = {
                    val xValues = mutableListOf<String>()
                    val dataSize = data?.dataSet?.size ?: 0
                    for(i in 0..dataSize) {
                        if (i % 5 == 0) {
                            data?.dataSet?.get(i)?.x?.labelize()?.let { xValues.add(it) }
                        }
                    }
                    xValues
                },
                xValueStyleModifier = { index:Int, _:Int ->
                    if (index == style?.xGridValues?.invoke()?.lastIndex) {
                        BarChartData.XValueStyle(
                            textColor = ContextCompat.getColor(this@MainActivity, R.color.black),
                            boldText = true)
                    } else {
                        BarChartData.XValueStyle(
                            textColor = ContextCompat.getColor(this@MainActivity, R.color.light_gray),
                            boldText = false)
                    }
                },
                barStyleModifier =  { index:Int, _:Int ->
                    val color = when(index) {
                        data?.dataSet?.lastIndex -> R.color.dark_gray
                        else -> if (data?.dataSet?.get(index)?.y == 0) {
                            R.color.dark_gray
                        } else {
                            R.color.tado
                        }
                    }
                    BarChartData.BarStyle(ContextCompat.getColor(this@MainActivity, color))
                }
            )

            data = Data(
                dataSet = list,
                yGuides = guides.map {
                    YGuide(it.meanInSecondsPerDay, "${it.meanInSecondsPerDay/3600}h")
                }
            )
        }
    }
}
