package com.example.warehouse.util

import android.animation.ValueAnimator
import android.provider.Settings
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView

/** Các extension animation ngắn, dùng lại ở nhiều màn hình. */

/** Chạy số từ [from] đến [to] trong [duration]ms, hiển thị qua [render] (ví dụ định dạng tiền). */
fun TextView.animateCount(from: Long, to: Long, duration: Long = 800L, render: (Long) -> String) {
    if (!areAnimationsEnabled()) {
        text = render(to)
        return
    }
    ValueAnimator.ofFloat(from.toFloat(), to.toFloat()).apply {
        this.duration = duration
        interpolator = DecelerateInterpolator()
        addUpdateListener { text = render((it.animatedValue as Float).toLong()) }
        start()
    }
}

/** Fade + trượt nhẹ từ dưới lên, dùng cho item danh sách; [delay] để các item lệch nhau. */
fun View.fadeSlideIn(delay: Long = 0L): ViewPropertyAnimator {
    alpha = 0f
    translationY = 24f
    return animate()
        .alpha(1f)
        .translationY(0f)
        .setStartDelay(delay)
        .setDuration(250L)
}

/** Hiệu ứng nhấn nút: co nhẹ về 0.95 rồi trở lại 1. */
fun View.bounceOnClick() {
    animate().scaleX(0.95f).scaleY(0.95f).setDuration(80L).withEndAction {
        animate().scaleX(1f).scaleY(1f).setDuration(80L).start()
    }.start()
}

/** Rung ngang khi đăng nhập sai. */
fun View.shake() {
    if (!areAnimationsEnabled()) return
    val animator = ValueAnimator.ofFloat(0f, 1f)
    animator.duration = 400L
    val offsets = floatArrayOf(0f, -12f, 12f, -8f, 8f, -4f, 4f, 0f)
    animator.addUpdateListener {
        val step = (it.animatedFraction * (offsets.size - 1)).toInt().coerceIn(0, offsets.size - 1)
        translationX = offsets[step]
    }
    animator.start()
}

/** Tôn trọng cài đặt giảm/chặn chuyển động của hệ thống (Settings > Accessibility). */
private fun View.areAnimationsEnabled(): Boolean =
    Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f
