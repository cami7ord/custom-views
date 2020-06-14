package com.example.widgets.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.widgets.R
import kotlin.math.absoluteValue

class HeatingTimeChartView : View {

    private var zeroLineY = 0.0f
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
    var zeroLineColor: Int = R.color.light_gray
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var zeroLineWeight: Float = 2f
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

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
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
                positiveBarsColor = ta.getColor(R.styleable.HeatingTimeChartView_positiveBarsColor, positiveBarsColor)
                negativeBarsColor = ta.getColor(R.styleable.HeatingTimeChartView_negativeBarsColor, negativeBarsColor)
                averageLineColor = ta.getColor(R.styleable.HeatingTimeChartView_averageLineColor, averageLineColor)
                zeroLineColor = ta.getColor(R.styleable.HeatingTimeChartView_zeroLineColor, zeroLineColor)
                zeroLineWeight = ta.getDimension(R.styleable.HeatingTimeChartView_zeroLineWeight, zeroLineWeight)
                averageLineWeight = ta.getDimension(R.styleable.HeatingTimeChartView_averageLineWeight, averageLineWeight)
                averageIntervalOn = ta.getDimension(R.styleable.HeatingTimeChartView_averageIntervalOn, averageIntervalOn)
                averageIntervalOff = ta.getDimension(R.styleable.HeatingTimeChartView_averageIntervalOff, averageIntervalOff)
            } finally {
                ta.recycle()
            }
        }
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        zeroLineY = measuredHeight / 2.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (items.isNotEmpty()) {
            onDrawLines(canvas)

            onDrawZeroLine(canvas)

            onDrawAverageLine(canvas)
        }

    }

    private fun onDrawZeroLine(canvas: Canvas) {

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = zeroLineWeight
        paint.pathEffect = null
        paint.color = zeroLineColor

        canvas.drawLine(0f, zeroLineY, measuredWidth.toFloat(), zeroLineY, paint)

    }

    private fun onDrawAverageLine(canvas: Canvas) {

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = averageLineWeight
        paint.pathEffect = DashPathEffect(floatArrayOf(averageIntervalOn, averageIntervalOff), 0f)
        paint.color = averageLineColor

        canvas.drawLine(
            0f, zeroLineY - zeroLineY * averageReturn * scalingFactor,
            measuredWidth.toFloat(), zeroLineY - zeroLineY * averageReturn * scalingFactor, paint
        )

    }

    private fun onDrawLines(canvas: Canvas) {
        val interval: Float = measuredWidth / (items.size + 1).toFloat()
        var step = 0f

        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = barWidth
        paint.pathEffect = null

        for (value in items) {
            step += interval
            onDrawSingleLine(step, value, canvas)
        }
    }

    private fun onDrawSingleLine(left: Float, value: Float, canvas: Canvas) {

        val top: Float
        val bottom: Float

        canvas.save()

        when {
            value > 0 -> {
                paint.color = positiveBarsColor
                top = zeroLineY - (zeroLineY * value) * scalingFactor
                bottom = zeroLineY
                canvas.clipRect(0f, 0f, measuredWidth.toFloat(), zeroLineY)
            }
            value < 0 -> {
                paint.color = negativeBarsColor
                top = zeroLineY
                bottom = zeroLineY + (zeroLineY * value).absoluteValue * scalingFactor
                canvas.clipRect(0f, zeroLineY, measuredWidth.toFloat(), measuredHeight.toFloat())
            }
            else -> return
        }

        canvas.scale(1f, 1f - (paint.strokeWidth / measuredHeight), 0f, zeroLineY)
        canvas.drawLine(left, top, left, bottom, paint)
        canvas.restore()

    }

    companion object {

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
            29.6f,
            11.39f
        ).map { x -> x/100 }
    }

}
