package com.example.widgets.charts

import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.util.Calendar

class HeatingTimeChartViewTest {

    @Test
    fun `30 days showing 5 by 5 days`() {
        val current = Calendar.getInstance()
        current.set(2020, 5, 16)
        val resultArray = HeatingTimeChartView.generateXaxisValues(current.time)
        assertArrayEquals(intArrayOf(17,22,27,1,6,11,16), resultArray)
    }

}
