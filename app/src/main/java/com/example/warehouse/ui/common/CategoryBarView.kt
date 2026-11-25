package com.example.warehouse.ui.common

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.warehouse.data.DatabaseHelper
import com.example.warehouse.util.AppConstants

/**
 * Biểu đồ thanh ngang tỉ lệ giá trị kho theo loại, tự vẽ bằng Canvas (không dùng thư viện).
 * Mỗi loại một màu theo [AppConstants.colorResForCategory].
 */
class CategoryBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class Bar(val label: String, val value: Long, val color: Int)

    private var bars: List<Bar> = emptyList()
    private var progress = 0f

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, com.example.warehouse.R.color.text_secondary)
        textSize = 28f
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, com.example.warehouse.R.color.text_primary)
        textSize = 28f
    }

    private var maxValue = 0L

    /** @param maxBars giới hạn số dòng hiển thị (ví dụ khi thu gọn), mặc định hiển thị hết. */
    fun setData(values: List<DatabaseHelper.CategoryValue>, maxBars: Int = Int.MAX_VALUE) {
        maxValue = values.maxOfOrNull { it.value } ?: 0L
        bars = values
            .filter { it.value > 0 }
            .sortedByDescending { it.value }
            .take(maxBars)
            .map { Bar(it.category, it.value, ContextCompat.getColor(context, AppConstants.colorResForCategory(it.category))) }
        animateIn()
        requestLayout()
        invalidate()
    }

    private fun animateIn() {
        progress = 0f
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 600L
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val rowHeight = (44 * resources.displayMetrics.density).toInt()
        val height = rowHeight * bars.size.coerceAtLeast(1)
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bars.isEmpty() || maxValue <= 0) return

        val rowHeight = height.toFloat() / bars.size
        val barHeight = rowHeight * 0.45f
        val labelWidth = width * 0.3f
        val maxBarWidth = width - labelWidth - 90f

        bars.forEachIndexed { index, bar ->
            val top = rowHeight * index + (rowHeight - barHeight) / 2f
            val bottom = top + barHeight

            canvas.drawText(bar.label, 0f, top + barHeight * 0.75f, labelPaint)

            val fraction = bar.value.toFloat() / maxValue.toFloat()
            val barWidth = maxBarWidth * fraction * progress
            barPaint.color = bar.color
            canvas.drawRoundRect(
                labelWidth, top, labelWidth + barWidth, bottom, barHeight / 2f, barHeight / 2f, barPaint
            )

            canvas.drawText(
                "${(fraction * 100).toInt()}%",
                labelWidth + barWidth + 12f,
                top + barHeight * 0.75f,
                valuePaint
            )
        }
    }
}
