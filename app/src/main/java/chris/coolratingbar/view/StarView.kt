package chris.coolratingbar.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Path
import android.util.AttributeSet

/**
 * @author chenchris on 2018/4/5.
 */
class StarView : BaseView {
    private val starPath = Path()
    private val cornerPathEffect: CornerPathEffect = CornerPathEffect(6f)
    private var starVertex: FloatArray? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        basePaint.pathEffect = cornerPathEffect
        borderPaint.pathEffect = cornerPathEffect
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (width == 0 || height == 0) return

        val bottomFromMargin = width * 0.2f
        val triangleSide = width * 0.35f
        val half = width * 0.5f
        val tipVerticalMargin = width * 0.05f
        val tipHorizontalMargin = width * 0.03f
        val innerUpHorizontalMargin = width * 0.38f
        val innerBottomHorizontalMargin = width * 0.32f
        val innerBottomVerticalMargin = width * 0.6f
        val innerCenterVerticalMargin = width * 0.27f

        starVertex = floatArrayOf(tipHorizontalMargin, innerUpHorizontalMargin, // top left
                tipHorizontalMargin + triangleSide, innerUpHorizontalMargin, half, tipVerticalMargin, // top tip
                width - tipHorizontalMargin - triangleSide, innerUpHorizontalMargin, width - tipHorizontalMargin, innerUpHorizontalMargin, // top right
                width - innerBottomHorizontalMargin, innerBottomVerticalMargin, width - bottomFromMargin, width - tipVerticalMargin, // bottom right
                half, width - innerCenterVerticalMargin, bottomFromMargin, width - tipVerticalMargin, // bottom left
                innerBottomHorizontalMargin, innerBottomVerticalMargin)
    }

    override fun drawShape(canvas: Canvas, cx: Float, cy: Float, filled: Float) {
        starVertex?.let {
            // calculate fill in pixels
            val fill = width * filled

            val width = this.width.toFloat()
            val height = this.height.toFloat()

            // prepare path for star
            starPath.apply {
                reset()
                moveTo(cx + it[0], cy + it[1])
                var i = 2
                while (i < it.size) {
                    lineTo(cx + it[i], cy + it[i + 1])
                    i += 2
                }
                close()
            }

            canvas.apply {
                // draw star outline
                drawPath(starPath, basePaint)

                if (shapeFillType == INCREASE_TYPE_VERTICAL) {
                    //vertical fill color
                    drawRect(cx, cy + height - fill, cx + width, cy + height, fillPaint)
                    drawRect(cx, 0f, cx + width, cy + height - fill, backgroundPaint)
                } else {
                    //horizontal fill color
                    drawRect(cx, cy, cx + fill, cy + height, fillPaint)
                    drawRect(cx + fill, cy, width, cy + height, backgroundPaint)
                }

                // draw star border on top
                drawPath(starPath, borderPaint)
            }
        }
    }
}