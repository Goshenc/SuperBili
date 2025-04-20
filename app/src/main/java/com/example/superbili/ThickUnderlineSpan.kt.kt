import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan

class ThickUnderlineSpan(
    private val color: Int,
    private val thickness: Float
) : LineBackgroundSpan {

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        val originalColor = paint.color
        val originalStrokeWidth = paint.strokeWidth

        // 设置下划线样式
        paint.color = color
        paint.strokeWidth = thickness

        // 计算下划线的 Y 轴位置（位于文字底部）
        val y = baseline.toFloat() + paint.descent() + 2.dpToPx() // 调整这个偏移量

        // 绘制线段
        canvas.drawLine(left.toFloat(), y, right.toFloat(), y, paint)

        // 恢复原始画笔设置
        paint.color = originalColor
        paint.strokeWidth = originalStrokeWidth
    }

    private fun Int.dpToPx(): Float = this * Resources.getSystem().displayMetrics.density
}