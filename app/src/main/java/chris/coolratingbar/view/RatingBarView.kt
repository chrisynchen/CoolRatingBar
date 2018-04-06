package chris.coolratingbar.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import chris.coolratingbar.R

/**
 * @author chenchris on 2018/4/6.
 */
class RatingBarView : LinearLayout {

    private val TAG = javaClass.simpleName

    companion object {
        const val INCREASE_TYPE_HORIZONTAL = BaseView.INCREASE_TYPE_HORIZONTAL
        const val INCREASE_TYPE_VERTICAL = BaseView.INCREASE_TYPE_VERTICAL
        const val SHAPE_STAR = 0
        const val SHAPE_HEART = 1
        const val DEFAULT_SHAPE_SIZE = 100
        const val DEFAULT_SHAPE_GAP_SIZE = 0
    }

    private val rateable = false
    private var shapeCount = 0
    private var totalWidth = 0
    private var totalHeight = 0

    private var isTouching = false

    private var onClickListener: View.OnClickListener? = null

    private var rating = 0.0f
    private var shapeViewGapWidth = 0
    private var shapeViewWidth = 0
    private var shapeViewHeight = 0
    private var shapeFillType = INCREASE_TYPE_HORIZONTAL
    private var shape = SHAPE_STAR

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.CoolRatingBar, defStyleAttr, 0)
        shapeFillType = a.getInt(
                R.styleable.CoolRatingBar_shapeFillType, INCREASE_TYPE_HORIZONTAL)
        shape = a.getInt(R.styleable.CoolRatingBar_shape, SHAPE_STAR)
        shapeViewGapWidth = a.getDimensionPixelSize(R.styleable.CoolRatingBar_shapeViewGapWidth, DEFAULT_SHAPE_GAP_SIZE)
        shapeViewWidth = a.getDimensionPixelSize(R.styleable.CoolRatingBar_shapeViewWidth, 0)
        shapeViewHeight = a.getDimensionPixelSize(R.styleable.CoolRatingBar_shapeViewHeight, 0)

        shapeCount = 5

        //add padding size of this view
        totalWidth += paddingLeft
        totalWidth += paddingRight
        totalHeight += paddingTop
        totalHeight += paddingBottom

        //add each shape view size
        for (i in 1..shapeCount) {
            val shapeView: BaseView = if (shape == SHAPE_HEART) {
                HeartView(context)
            } else {
                StarView(context)
            }

            if (shapeViewWidth == 0 && shapeViewHeight == 0) {
                shapeViewWidth = DEFAULT_SHAPE_SIZE
                shapeViewHeight = DEFAULT_SHAPE_SIZE
            } else if (shapeViewWidth == 0) {
                shapeViewWidth = shapeViewHeight
            } else if (shapeViewHeight == 0) {
                shapeViewHeight = shapeViewWidth
            }

            val shapeViewLayoutParams = LayoutParams(shapeViewWidth, shapeViewHeight)

            when (i) {
                1 -> shapeViewLayoutParams.setMargins(0, 0, shapeViewGapWidth / 2, 0)
                shapeCount -> shapeViewLayoutParams.setMargins(shapeViewGapWidth / 2, 0, 0, 0)
                else -> shapeViewLayoutParams.setMargins(shapeViewGapWidth / 2, 0, shapeViewGapWidth / 2, 0)
            }
            shapeView.layoutParams = shapeViewLayoutParams
            shapeView.setFilled(0f)
            shapeView.setIndex(i)
            addView(shapeView)
            totalWidth += shapeViewWidth
        }
    }

    override fun setOnClickListener(listener: View.OnClickListener?) {
        this.onClickListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (rateable) return false

        val action = event.action and MotionEvent.ACTION_MASK

        when (action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                isTouching = true
                setRatingValue(event.x, event.y)
                updateShapeViews()
            }

            MotionEvent.ACTION_UP -> {
                setRatingValue(event.x, event.y)
                updateShapeViews()
                onClickListener?.onClick(this)
            }

            MotionEvent.ACTION_CANCEL -> {

            }
        }
        return true
    }

    private fun setRatingValue(cx: Float, cy: Float): Float {
        val quotient = ((cx - paddingLeft) / (shapeViewWidth + shapeViewGapWidth)).toInt()
        val remainder = (cx - paddingLeft) % (shapeViewWidth + shapeViewGapWidth)
        if (quotient >= shapeCount) {
            rating = shapeCount.toFloat()
        } else {
            rating = quotient + Math.min(1f, (remainder / shapeViewWidth))
        }

        Log.e(TAG, "rating:" + rating.toString()
                + " remainder:" + remainder + " quotient:" + quotient)
        return rating
    }

    private fun updateShapeViews() {
        (0..childCount)
                .map { getChildAt(it) }
                .filterIsInstance<BaseView>()
                .forEach {
                    if (it.getIndex() <= rating) {
                        it.setFilled(1f)
                    } else if (it.getIndex() - rating > 1) {
                        it.setFilled(0f)
                    } else {
                        it.setFilled(rating - rating.toInt())
                    }

                    it.invalidate()
                }
    }

    private fun setShapeViewGapWidth(gap: Int) {
        this.shapeViewGapWidth = gap
    }

    private fun setRating(rating: Float) {
        this.rating = rating
    }

    private fun setShapeCount(count: Int) {
        this.shapeCount = count
    }

    public fun getShapeViewGapWidth(): Int {
        return shapeViewGapWidth
    }

    public fun getRating(): Float {
        return rating
    }

    public fun getShapeCount(): Int {
        return shapeCount
    }
}