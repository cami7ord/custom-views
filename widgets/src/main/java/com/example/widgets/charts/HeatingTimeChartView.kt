package com.example.widgets.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.widgets.R
import java.util.*

class HeatingTimeChartView : View {

    private var borderTopY = 8f
    private var borderLeftX = 8f
    private var borderBottomY = 0f
    private var borderRightX = 0f
    private var lineSeparationY = 0f
    private var lineSeparationX = 0f
    private var averageReturn = chartValues.average().toFloat()

    private val paint = Paint()

    var items: List<Float> = chartValues
        set(value) {
            field = value
            averageReturn = value.average().toFloat()
            invalidate()
        }

    var scalingFactor: Float = 1.0f
        set(value) {
            field = value
            invalidate()
        }
    var barWidth: Float = 16f
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var positiveBarsColor: Int = R.color.tado
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var negativeBarsColor: Int = R.color.light_gray
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var averageLineColor: Int = R.color.black
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var averageLineWeight: Float = 4f
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var averageIntervalOn: Float = 14f
        set(value) {
            field = value
            invalidate()
        }
    var averageIntervalOff: Float = 8f
        set(value) {
            field = value
            invalidate()
        }
    var borderlineColor: Int = R.color.tado
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var borderlineWeight: Float = 8f
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    constructor(context: Context?) : super(context) {
        initialize(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.HeatingTimeChartView)
            try {
                barWidth = ta.getDimension(R.styleable.HeatingTimeChartView_barWidth, barWidth)
                positiveBarsColor = ta.getColor(
                    R.styleable.HeatingTimeChartView_positiveBarsColor,
                    positiveBarsColor
                )
                negativeBarsColor = ta.getColor(
                    R.styleable.HeatingTimeChartView_negativeBarsColor,
                    negativeBarsColor
                )
                averageLineColor =
                    ta.getColor(R.styleable.HeatingTimeChartView_averageLineColor, averageLineColor)
                borderlineColor =
                    ta.getColor(R.styleable.HeatingTimeChartView_borderlineColor, borderlineColor)
                borderlineWeight = ta.getDimension(
                    R.styleable.HeatingTimeChartView_borderlineWeight,
                    borderlineWeight
                )
                averageLineWeight = ta.getDimension(
                    R.styleable.HeatingTimeChartView_averageLineWeight,
                    averageLineWeight
                )
                averageIntervalOn = ta.getDimension(
                    R.styleable.HeatingTimeChartView_averageIntervalOn,
                    averageIntervalOn
                )
                averageIntervalOff = ta.getDimension(
                    R.styleable.HeatingTimeChartView_averageIntervalOff,
                    averageIntervalOff
                )
            } finally {
                ta.recycle()
            }
        }
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        paint.color = ContextCompat.getColor(context, R.color.light_gray) // TODO use defaults
        paint.textSize = 50f

        //Order is important
        borderBottomY = measuredHeight - borderTopY - paint.textSize * 2
        lineSeparationY = borderBottomY / 3f
        lineSeparationX = measuredWidth / chartValues.size.toFloat()
        borderRightX = measuredWidth - paint.measureText("24h") - lineSeparationX
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //TODO son 31 espacios, dividir por 31 el ancho

        if (items.isNotEmpty()) {

            onDrawGrid(canvas)

            /*onDrawLines(canvas)

            onDrawZeroLine(canvas)

            onDrawAverageLine(canvas)*/
        }

    }

    private fun onDrawGrid(canvas: Canvas) {

        paint.pathEffect = null

        onDrawYAxis(canvas)
        onDrawXAxis(canvas)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderlineWeight

        canvas.drawRect(borderLeftX, borderTopY, borderRightX, borderBottomY, paint)
        //canvas.drawLine(0f, zeroLineY, measuredWidth.toFloat(), zeroLineY, paint)

    }

    private fun onDrawYAxis(canvas: Canvas) {
        var textY = 0f
        val lastIndex = generateYaxisValues().lastIndex
        val tempW = paint.strokeWidth
        generateYaxisValues().forEachIndexed { index, i ->
            textY = when (index) {
                0 -> paint.textSize
                lastIndex -> borderBottomY
                else -> {
                    paint.strokeWidth = borderlineWeight
                    canvas.drawLine(
                        borderRightX,
                        lineSeparationY * index,
                        borderLeftX,
                        lineSeparationY * index,
                        paint
                    )
                    (lineSeparationY * index) + (paint.textSize / 2)
                }
            }
            paint.strokeWidth = tempW
            canvas.drawText("${i}h", borderRightX + lineSeparationX, textY, paint)
        }
    }

    private fun onDrawXAxis(canvas: Canvas) {
        var textX = lineSeparationX
        val textY = measuredHeight.toFloat() - paint.textSize + (paint.textSize / 2)
        generateXaxisValues(Calendar.getInstance().time).forEach {
            canvas.drawText("$it", textX - (paint.measureText(it.toString()) / 2), textY, paint)
            textX += lineSeparationX * 4f
        }
    }

    companion object {

        fun generateYaxisValues(): IntArray {
            return yAxisValues
        }

        fun generateXaxisValues(date: Date): IntArray {
            val results = IntArray(7)
            val calendar = Calendar.getInstance()
            calendar.time = date
            for (i in 6.downTo(0)) {
                results[i] = calendar.get(Calendar.DAY_OF_MONTH)
                calendar.add(Calendar.DAY_OF_MONTH, -5)
            }
            return results
        }

        private val yAxisValues = intArrayOf(
            24,
            16,
            8,
            0
        )

        val chartValues = listOf(
            19.53f,
            26.38f,
            8.99f,
            3f,
            13.62f,
            3.53f,
            23.45f,
            12.78f,
            0f,
            13.41f,
            19.53f,
            26.38f,
            8.99f,
            3f,
            13.62f,
            3.53f,
            23.45f,
            12.78f,
            0f,
            13.41f,
            19.53f,
            26.38f,
            8.99f,
            3f,
            13.62f,
            3.53f,
            23.45f,
            12.78f,
            0f,
            13.41f
        ).map { x -> x / 100 }
    }

}
