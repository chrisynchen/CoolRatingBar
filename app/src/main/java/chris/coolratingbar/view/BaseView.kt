package chris.coolratingbar.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * @author chenchris on 2018/4/8.
 */
abstract class BaseView : View {

    private val TAG = javaClass.simpleName

    companion object {
        const val INCREASE_TYPE_HORIZONTAL = 0
        const val INCREASE_TYPE_VERTICAL = 1
    }

    protected val basePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    protected val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    protected val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    protected val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    private var sourceBitmap: Bitmap? = null
    private var croppedCanvas: Canvas? = null

    private var filled = 0f

    private var index = 0

    protected var shapeFillType = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        basePaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            isDither = true
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            color = Color.BLACK
        }

        backgroundPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            isDither = true
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            color = Color.YELLOW
            xfermode = if (color == Color.TRANSPARENT) {
                PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            } else {
                PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
            }
        }

        fillPaint.apply {
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            isDither = true
            color = Color.GREEN
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        }

        borderPaint.apply {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 5f
            color = Color.RED
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "widthMeasureSpec: " + MeasureSpec.getSize(widthMeasureSpec) + " heightMeasureSpec: " + MeasureSpec.getSize(heightMeasureSpec))
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)

        if (width == 0 || height == 0) return
        generateCroppedCanvas(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        croppedCanvas?.let {
            drawShape(it, 0f, 0f, filled)
        }

        canvas.drawBitmap(sourceBitmap, 0f, 0f, null)
    }

    /**
     * Generates cropped canvas on which the ratingbar will be drawn.
     * @param width
     * @param height
     */
    private fun generateCroppedCanvas(width: Int, height: Int) {
        // avoid leaking memory after losing the reference
        sourceBitmap?.recycle()

        // if width == 0 or height == 0 we don't need internal bitmap, cause view won't be drawn anyway.
        sourceBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        sourceBitmap?.eraseColor(Color.TRANSPARENT)
        croppedCanvas = Canvas(sourceBitmap)
    }

    fun setFilled(filled: Float) {
        this.filled = filled
    }

    fun setIndex(index: Int) {
        this.index = index
    }

    fun getIndex(): Int {
        return index
    }

    abstract fun drawShape(canvas: Canvas, cx: Float, cy: Float, filled: Float)
}