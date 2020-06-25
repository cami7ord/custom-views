package com.example.widgets.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.widgets.R

class BarChartView : View {

    private var borderTopY = 8f
    private var borderLeftX = 8f
    private var borderBottomY = 0f
    private var borderRightX = 0f
    private var lineSeparationX = 0f
    private var borderPadding = 0f

    private val paint = Paint()

    var props: BarChartData<*, *>? = null
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
    var guideLineColor: Int = R.color.tado
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var guideLineWeight: Float = 4f
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }
    var guideIntervalOn: Float = 14f
        set(value) {
            field = value
            invalidate()
        }
    var guideIntervalOff: Float = 8f
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
            val ta = context.obtainStyledAttributes(attrs, R.styleable.BarChartView)
            try {
                barWidth = ta.getDimension(R.styleable.BarChartView_barWidth, barWidth)
                regularBarsColor = ta.getColor(
                    R.styleable.BarChartView_regularBarsColor,
                    regularBarsColor
                )
                specialBarsColor = ta.getColor(
                    R.styleable.BarChartView_specialBarsColor,
                    specialBarsColor
                )
                guideLineColor =
                    ta.getColor(R.styleable.BarChartView_averageLineColor, guideLineColor)
                borderlineColor =
                    ta.getColor(R.styleable.BarChartView_borderlineColor, borderlineColor)
                borderlineWeight = ta.getDimension(
                    R.styleable.BarChartView_borderlineWeight,
                    borderlineWeight
                )
                guideLineWeight = ta.getDimension(
                    R.styleable.BarChartView_averageLineWeight,
                    guideLineWeight
                )
                guideIntervalOn = ta.getDimension(
                    R.styleable.BarChartView_averageIntervalOn,
                    guideIntervalOn
                )
                guideIntervalOff = ta.getDimension(
                    R.styleable.BarChartView_averageIntervalOff,
                    guideIntervalOff
                )
            } finally {
                ta.recycle()
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        paint.color = ContextCompat.getColor(context, R.color.light_gray)
        guideLineColor = ContextCompat.getColor(context, R.color.black)
        paint.textSize = resources.getDimension(R.dimen.text_normal)
        borderPadding = resources.getDimension(R.dimen.margin_half)
        borderBottomY = measuredHeight - borderTopY - paint.textSize * 2
        borderRightX = measuredWidth - paint.measureText(props?.style?.yGridLines?.get(0)?.label) - borderPadding
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
                    val h = borderBottomY - borderBottomY * convertValueToHeight(i.value)
                    canvas.drawLine(
                        borderLeftX,
                        h,
                        borderRightX,
                        h,
                        paint
                    )
                    h + (paint.textSize / 2)
                }
            }
            paint.strokeWidth = tempW
            canvas.drawText(i.label ?: "", borderRightX + borderPadding, textY, paint)
        }
    }

    private fun onDrawXAxis(canvas: Canvas) {
        var textX: Float
        val textY = measuredHeight.toFloat() - paint.textSize + borderPadding
        val values = props?.style?.xGridValues?.invoke() ?: emptyList()
        val xSeparation = if (values.isNotEmpty()) {
            (borderRightX / (values.size - 1))
        } else {
            0f
        }
        values.forEachIndexed { index, i ->
            textX = when(index) {
                0 -> lineSeparationX
                values.lastIndex ->  borderRightX-lineSeparationX
                else -> {
                    xSeparation * index
                }
            }
            val xStyle = props?.style?.xValueStyleModifier?.invoke(index, index)
            paint.color = xStyle?.textColor ?: guideLineColor
            paint.isFakeBoldText = xStyle?.boldText ?: false
            canvas.drawText(i, textX - (paint.measureText(i) / 2), textY, paint)
            paint.isFakeBoldText = false
        }
    }

    private fun onDrawBars(canvas: Canvas) {
        var step = 0f
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = barWidth

        props?.data?.dataSet?.forEachIndexed { index, value ->
            step += lineSeparationX
            paint.color = props?.style?.barStyleModifier?.invoke(index, index)?.color ?: regularBarsColor
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
                borderBottomY - (borderBottomY * convertValueToHeight(value))
            }
        }
        val bottom = borderBottomY
        canvas.clipRect(0f, 0f, measuredWidth.toFloat(), borderBottomY)

        canvas.scale(1f, 1f - (paint.strokeWidth / measuredHeight), 0f, borderBottomY)
        canvas.drawLine(left, top, left, bottom, paint)

        canvas.restore()

    }

    private fun onDrawGuides(canvas: Canvas) {

        paint.strokeWidth = guideLineWeight
        paint.color = guideLineColor
        paint.style = Paint.Style.FILL

        paint.isFakeBoldText = true
        props?.data?.yGuides?.forEach { guide ->
            canvas.drawText(
                guide.label,
                borderRightX + borderPadding,
                (borderBottomY - borderBottomY * convertValueToHeight(guide.value)) + (paint.textSize / 2) - 6f,
                paint
            )

            paint.style = Paint.Style.STROKE
            paint.pathEffect =
                DashPathEffect(floatArrayOf(guideIntervalOn, guideIntervalOff), 0f)

            canvas.drawLine(
                borderLeftX,
                borderBottomY - borderBottomY * convertValueToHeight(guide.value),
                borderRightX,
                borderBottomY - borderBottomY * convertValueToHeight(guide.value),
                paint
            )
        }
        paint.isFakeBoldText = false
    }

    private fun <YValue : Number> convertValueToHeight(value: YValue): Float {
        val maxBarValue: Float = props?.style?.yAxis?.max?.toFloat() ?: 1f
        return ((value.toFloat() * 100) / maxBarValue) / 100
    }

}
