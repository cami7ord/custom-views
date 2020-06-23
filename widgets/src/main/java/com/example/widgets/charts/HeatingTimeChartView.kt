package com.example.widgets.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.widgets.R

class HeatingTimeChartView : View {

    private var borderTopY = 8f
    private var borderLeftX = 8f
    private var borderBottomY = 0f
    private var borderRightX = 0f
    private var lineSeparationY = 0f
    private var lineSeparationX = 0f

    private val paint = Paint()

    var props: BarGraphData<*, *>? = null
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
    var regularBarsColor: Int = R.color.tado
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var specialBarsColor: Int = R.color.dark_gray
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
    var borderlineWeight: Float = 4f
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

    private fun initialize(attrs: AttributeSet?) {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.HeatingTimeChartView)
            try {
                barWidth = ta.getDimension(R.styleable.HeatingTimeChartView_barWidth, barWidth)
                regularBarsColor = ta.getColor(
                    R.styleable.HeatingTimeChartView_regularBarsColor,
                    regularBarsColor
                )
                specialBarsColor = ta.getColor(
                    R.styleable.HeatingTimeChartView_specialBarsColor,
                    specialBarsColor
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
        regularBarsColor = ContextCompat.getColor(context, R.color.tado)
        specialBarsColor = ContextCompat.getColor(context, R.color.dark_gray)

        //Order is important
        borderBottomY = measuredHeight - borderTopY - paint.textSize * 2
        lineSeparationY = (borderBottomY / ((props?.style?.yGridLines?.size ?: 1) - 1))
        borderRightX = measuredWidth - 16f - paint.measureText(props?.style?.yGridLines?.get(0)?.label) - 16f
        lineSeparationX = (borderRightX / ((props?.data?.dataSet?.size ?: 0) + 1))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (props != null) {

            onDrawGrid(canvas)

            onDrawBars(canvas)

            onDrawGuides(canvas)
        }

    }

    private fun onDrawGrid(canvas: Canvas) {
        onDrawYAxis(canvas)
        onDrawXAxis(canvas)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderlineWeight
        paint.color = ContextCompat.getColor(context, R.color.light_gray)

        canvas.drawRect(borderLeftX, borderTopY, borderRightX, borderBottomY, paint)
    }

    private fun onDrawYAxis(canvas: Canvas) {
        var textY: Float
        val lastIndex = props?.style?.yGridLines?.lastIndex ?: 0
        val tempW = paint.strokeWidth
        props?.style?.yGridLines?.forEachIndexed { index, i ->
            textY = when (index) {
                0 -> paint.textSize
                lastIndex -> borderBottomY
                else -> {
                    paint.strokeWidth = borderlineWeight
                    canvas.drawLine(
                        borderLeftX,
                        lineSeparationY * index,
                        borderRightX,
                        lineSeparationY * index,
                        paint
                    )
                    (lineSeparationY * index) + (paint.textSize / 2)
                }
            }
            paint.strokeWidth = tempW
            canvas.drawText(i.label ?: "", borderRightX, textY, paint)
        }
    }

    private fun onDrawXAxis(canvas: Canvas) {
        var textX = lineSeparationX
        val textY = measuredHeight.toFloat() - paint.textSize + (paint.textSize / 2)
        val values = props?.style?.xGridValues?.invoke() ?: emptyList()
        val xSeparation = if (values.isNotEmpty()) {
            borderRightX / values.size
        } else {
            0f
        }
        values.forEachIndexed { index, i ->
            if (index == values.lastIndex) {
                paint.color = averageLineColor
            }
            canvas.drawText(i, textX - (paint.measureText(i) / 2), textY, paint)
            textX += xSeparation
        }
    }

    private fun onDrawBars(canvas: Canvas) {
        var step = 0f
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = barWidth

        props?.data?.dataSet?.forEachIndexed { index, value ->
            step += lineSeparationX
            if (value.y == 0f) {
                paint.color = specialBarsColor
            } else {
                paint.color = regularBarsColor
            }
            onDrawSingleBar(step, value.y?.toFloat(), canvas)
        }
    }

    private fun onDrawSingleBar(left: Float, value: Float?, canvas: Canvas) {

        canvas.save()

        val top = when (value) {
            0f -> borderBottomY - (2.5f)
            null -> {
                return
            }
            else -> {
                borderBottomY - (borderBottomY * convertValueToBarHeight(value))
            }
        }
        val bottom = borderBottomY
        canvas.clipRect(0f, 0f, measuredWidth.toFloat(), borderBottomY)

        canvas.scale(1f, 1f - (paint.strokeWidth / measuredHeight), 0f, borderBottomY)
        canvas.drawLine(left, top, left, bottom, paint)

        canvas.restore()

    }

    private fun onDrawGuides(canvas: Canvas) {

        paint.strokeWidth = averageLineWeight
        paint.color = averageLineColor
        paint.style = Paint.Style.FILL

        props?.data?.yGuides?.forEach { guide ->
            canvas.drawText(
                guide.label,
                borderRightX + lineSeparationX,
                (borderBottomY - borderBottomY * convertValueToBarHeight(guide.value)) + (paint.textSize / 2) - 6f,
                paint
            )

            paint.style = Paint.Style.STROKE
            paint.pathEffect =
                DashPathEffect(floatArrayOf(averageIntervalOn, averageIntervalOff), 0f)

            canvas.drawLine(
                borderLeftX,
                borderBottomY - borderBottomY * convertValueToBarHeight(guide.value),
                borderRightX,
                borderBottomY - borderBottomY * convertValueToBarHeight(guide.value),
                paint
            )
        }
    }

    private fun <YValue : Number> convertValueToBarHeight(value: YValue): Float {
        val maxBarValue: Float = props?.style?.yAxis?.max?.toFloat() ?: 1f
        return ((value.toFloat() * 100) / maxBarValue) / 100
    }

}
