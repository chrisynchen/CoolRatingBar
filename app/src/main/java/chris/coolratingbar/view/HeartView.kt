package chris.coolratingbar.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet

/**
 * @author chenchris on 2018/4/5.
 */
class HeartView : BaseView {

    private val heartPath = Path()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun drawShape(canvas: Canvas, cx: Float, cy: Float, filled: Float) {
        val fill = width * filled

        val width = this.width.toFloat()
        val height = this.height.toFloat()

        heartPath.apply {
            reset()
            moveTo(width / 2, height / 5)

            // Upper left path
            cubicTo(5 * width / 14, 0f,
                    0f, height / 15,
                    width / 28, 2 * height / 5)

            // Lower left path
            cubicTo(width / 14, 2 * height / 3,
                    3 * width / 7, 5 * height / 6,
                    width / 2, height)

            // Lower right path
            cubicTo(4 * width / 7, 5 * height / 6,
                    13 * width / 14, 2 * height / 3,
                    27 * width / 28, 2 * height / 5)

            // Upper right path
            cubicTo(width, height / 15,
                    9 * width / 14, 0f,
                    width / 2, height / 5)

            close()
        }

        canvas.apply {
            drawPath(heartPath, basePaint)
            if (shapeFillType == INCREASE_TYPE_VERTICAL) {
                drawRect(0f + fill, 0f, width, 0f + height, backgroundPaint)
                drawRect(0f, 0f, 0f + fill, 0f + height, fillPaint)
            } else {
                drawRect(0f + fill, 0f, width, 0f + height, backgroundPaint)
                drawRect(0f, 0f, 0f + fill, 0f + height, fillPaint)
            }

            // draw star border on top
            drawPath(heartPath, borderPaint)
        }
    }
}