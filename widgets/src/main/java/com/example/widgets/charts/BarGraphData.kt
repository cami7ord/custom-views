package com.example.widgets.charts

class BarGraphData<XValue : Any, YValue : Number> {

    var data: Data? = null

    data class DataSetEntry<XValue, YValue>(  // Data representation of the value
        val x: XValue, // X affects rendered label for given data set entry
        val y: YValue? // Y defines how tall the bar should be
    )

    inner class YGuide( // Horizontal guide at given value with label next to the Y axis
        val value: YValue, // Y value where the guide line should be rendered at
        val label: String // Label which should be rendered next to the Y axis
    )

    inner class Data( // Raw data to render
        val dataSet: List<DataSetEntry<XValue, YValue>>, // List of bar (columns)
        val yGuides: List<YGuide> = emptyList() // List of horizontal guides (for example for AVG line)
    )

}