package com.example.widgets.charts

class BarGraphData<XValue: BarGraphData.Labelizable, YValue : Number> {

    var style: Style? = null
    var data: Data? = null

    inner class Style( // Global style for
        // OTHER UI STYLE COLORS/MODIFIERS WILL COME HERE
        val yAxis: YAxis, // Fixed configuration for Y axis
        val yGridLines: List<YGridLine>, // Fixed list of horizontal grid lines
        val xGridValues: () -> List<String>
        //val xValueFormatter: (value: XValue, index: Int, count: Int) -> String? // Custom formatter converting XValue to String

    )

    inner class YAxis( // Options for Y axis - range
        val min: YValue, // Minimal value of the Y axis
        val max: YValue // Maximal value of the Y axis
    )

    inner class YGridLine( // Horizontal line at given value with optional label next to the Y axis
        val value: YValue, // Y value where the grid line should be rendered at
        val label: String? // Label which could be rendered next to the Y axis
    )

    // region Data

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

    interface Labelizable {
        fun labelize(): String?
    }

    // endregion
}
