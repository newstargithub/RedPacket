package com.halo.redpacket.widget.viewtip

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.*
import android.text.Html
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.TextView
import com.halo.redpacket.R
import com.halo.redpacket.util.ThemeUtils
import kotlin.math.max
import kotlin.math.min

open class TooltipView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val MARGIN_SCREEN_BORDER_TOOLTIP = 30
        const val ARROW_HEIGHT = 15
    }

    /**
     * 所依附的View的位置
     */
    private var mViewRect: Rect? = null
    private var mAutoHide: Boolean = true
    private var mDuration: Long = 4000
    private var mTooltipAnimation: TooltipAnimation? = ViewTooltip.FadeTooltipAnimation()
    private var mListenerHide: ListenerHide? = null
    private var mListenerDisplay: ListenerDisplay? = null
    private var mCorner: Int = 0
    private var mClickToHide: Boolean = false
    private var mAlign: ALIGN = ALIGN.CENTER
    private var mPosition: Position = Position.BOTTOM
    var mPaddingLeft: Int = 0
    var mPaddingRight: Int = 0
    var mPaddingBottom: Int = 0
    var mPaddingTop: Int = 0
    private var mChildView: View
    private var mBubbleColor = Color.parseColor("#B2299EE3")
    private var mBubblePath: Path? = null
    private var mBubblePaint: Paint

    init {
        setWillNotDraw(false)

        mChildView = TextView(context)
        (mChildView as TextView).setTextColor(Color.WHITE)
        addView(mChildView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        mChildView.setPadding(0, 0, 0, 0)

        mBubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)//抗锯齿
        mBubblePaint.color = mBubbleColor
        mBubblePaint.style = Paint.Style.FILL

        mPaddingTop = ThemeUtils.resolveDimension(getContext(), R.attr.xui_tip_popup_padding_top).also { mPaddingBottom = it }
        mPaddingRight = ThemeUtils.resolveDimension(getContext(), R.attr.xui_tip_popup_padding_left).also { mPaddingLeft = it }
    }

    /**
     * 设置自定义提示布局
     * @param customView
     * @return
     */
    fun setCustomView(view: View) {
        removeView(mChildView)
        mChildView = view
        addView(mChildView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    fun setColor(color: Int) {
        mBubbleColor = color
        mBubblePaint.color = color
        postInvalidate()
    }

    /**
     * 设置提示显示的相对位置
     * @param position
     * @return
     */
    fun setPosition(position: Position) {
        mPosition = position
        when(position) {
            Position.TOP -> {
                setPaddingRelative(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom + ARROW_HEIGHT)
            }
            Position.BOTTOM -> {
                setPaddingRelative(mPaddingLeft, mPaddingTop + ARROW_HEIGHT, mPaddingRight, mPaddingBottom)
            }
            Position.LEFT -> {
                setPaddingRelative(mPaddingLeft, mPaddingTop, mPaddingRight + ARROW_HEIGHT, mPaddingBottom)
            }
            Position.RIGHT -> {
                setPaddingRelative(mPaddingLeft + ARROW_HEIGHT, mPaddingTop, mPaddingRight, mPaddingBottom)
            }
        }
        postInvalidate()
    }

    fun setAlign(align: ALIGN) {
        mAlign = align
        postInvalidate()
    }

    fun setText(text: String) {
        if (mChildView is TextView) {
            (mChildView as TextView).text = Html.fromHtml(text)
        }
        postInvalidate()
    }

    fun setTextColor(color: Int) {
        if (mChildView is TextView) {
            (mChildView as TextView).setTextColor(color)
        }
        postInvalidate()
    }

    fun setTextSize(unit: Int, size: Float) {
        if (mChildView is TextView) {
            (mChildView as TextView).setTextSize(unit, size)
        }
        postInvalidate()
    }

    fun setTextGravity(gravity: Int) {
        if (mChildView is TextView) {
            (mChildView as TextView).gravity = gravity
        }
        postInvalidate()
    }

    fun setClickToHide(clickToHide: Boolean) {
        mClickToHide = clickToHide
    }

    fun setCorner(corner: Int) {
        mCorner = corner
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBubblePath = drawBubble(RectF(0f, 0f, w.toFloat(), h.toFloat()), mCorner, mCorner, mCorner, mCorner)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mBubblePath?.let {
            canvas.drawPath(it, mBubblePaint)
        }
    }

    fun setListenerDisplay(listener: ListenerDisplay?) {
        mListenerDisplay = listener
    }

    fun setListenerHide(listener: ListenerHide?) {
        mListenerHide = listener
    }

    fun setTooltipAnimation(tooltipAnimation: TooltipAnimation?) {
        mTooltipAnimation = tooltipAnimation
    }

    protected fun startEnterAnimation() {
        mTooltipAnimation?.animateEnter(this, object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                mListenerDisplay?.onDisplay(this@TooltipView)
            }
        })
    }

    protected fun startExitAnimation(animatorListener: AnimatorListener) {
        mTooltipAnimation?.animateExit(this, object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animatorListener.onAnimationEnd(animation)
                mListenerHide?.onHide(this@TooltipView)
            }
        })
    }

    /**
     * 处理移除
     */
    protected fun handleAutoRemove() {
        if (mClickToHide) {
            setOnClickListener {
                if (mClickToHide) {
                    remove()
                }
            }
        }
        if (mAutoHide) {
            postDelayed({
                remove()
            }, mDuration)
        }
    }

    fun remove() {
        startExitAnimation(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                parent.apply {
                    removeView(this@TooltipView)
                }
            }
        })
    }

    fun setDuration(duration: Long) {
        mDuration = duration
    }

    fun setAutoHide(autoHide: Boolean) {
        mAutoHide = autoHide
    }

    fun close() {
        remove()
    }

    private fun setupPosition(rect: Rect) {
        if (mPosition == Position.LEFT || mPosition == Position.RIGHT) {
            val height = height
            val hisHeight = rect.height()
            val minHeight = min(height, hisHeight)
            val maxHeight = max(height, hisHeight)
            var spacingY = 0
            when(mAlign) {
                ALIGN.START -> {

                }
                ALIGN.CENTER -> {
                    spacingY = (-1F * maxHeight / 2F + minHeight / 2F).toInt()
                }
            }
            if (mPosition == Position.LEFT) {
                translationY = (rect.top + spacingY).toFloat()
                translationX = (rect.left - width).toFloat()
            } else {
                translationY = rect.top + spacingY.toFloat()
                translationX = rect.right.toFloat()
            }
        } else {
            var spacingX = 0
            val width = width
            val hisWidth = rect.width()
            if (mAlign == ALIGN.CENTER) {
                spacingX = (hisWidth / 2f - width / 2f).toInt()
            }
            if (mPosition == Position.BOTTOM) {
                translationY = rect.bottom.toFloat()
                translationX = (rect.left + spacingX).toFloat()
            } else {
                translationY = (rect.top - height).toFloat()
                translationX = (rect.left + spacingX).toFloat()
            }
        }
    }

    fun setup(viewRect: Rect, screenWidth: Int) {
        mViewRect = Rect(viewRect)
        val myRect = Rect(viewRect)
        val changed = adjustSize(myRect, screenWidth)
        if (!changed) {
            onSetup(myRect)
        } else {
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    onSetup(myRect)
                    viewTreeObserver.removeOnPreDrawListener(this)
                    return false
                }
            })
        }
    }

    private fun adjustSize(viewRect: Rect, screenWidth: Int): Boolean {
        var changed = false
        val layoutParams = layoutParams
        if (mPosition == Position.LEFT && width > viewRect.left) {
            layoutParams.width = viewRect.left - MARGIN_SCREEN_BORDER_TOOLTIP
            changed = true
        } else if (mPosition == Position.RIGHT && viewRect.right + width > screenWidth) {
            layoutParams.width = screenWidth - viewRect.right - MARGIN_SCREEN_BORDER_TOOLTIP
            changed = true
        } else if (mPosition == Position.TOP || mPosition == Position.BOTTOM) {
            val widthRight = (width - viewRect.width()) / 2f
            if (viewRect.right + widthRight > screenWidth) {
                val movinX = (viewRect.right + widthRight - screenWidth + MARGIN_SCREEN_BORDER_TOOLTIP).toInt()
                viewRect.left -= movinX
                viewRect.right -= movinX
                changed = true
            } else if (viewRect.left - widthRight < 0) {
                val movinX = (- (viewRect.left - widthRight) + MARGIN_SCREEN_BORDER_TOOLTIP).toInt()
                viewRect.left += movinX.toInt()
                viewRect.right += movinX.toInt()
                changed = true
            }
        }
        setLayoutParams(layoutParams)
        postInvalidate()
        return changed
    }

    private fun onSetup(myRect: Rect) {
        setupPosition(myRect)
        mBubblePath = drawBubble(RectF(0f, 0f, width.toFloat(), height.toFloat()), mCorner, mCorner, mCorner, mCorner)
        startEnterAnimation()
        handleAutoRemove()
    }

    /**
     * 画边框
     *
     * @param rect
     * @param topLeftDiameter
     * @param topRightDiameter
     * @param bottomRightDiameter
     * @param bottomLeftDiameter
     * @return
     */
    private fun drawBubble(myRect: RectF, topLeftDiameter: Int, topRightDiameter: Int, bottomRightDiameter: Int, bottomLeftDiameter: Int): Path {
        val path = Path()

        if (mViewRect == null) {
            return path
        }

        val topLeftCorner = if (topLeftDiameter < 0) 0 else topLeftDiameter
        val topRightCorner = if (topRightDiameter < 0) 0 else topRightDiameter
        val bottomLeftCorner = if (bottomLeftDiameter < 0) 0 else bottomLeftDiameter
        val bottomRightCorner = if (bottomRightDiameter < 0) 0 else bottomRightDiameter

        val spacingLeft = if (mPosition == Position.RIGHT) ARROW_HEIGHT.toFloat() else 0.toFloat()
        val spacingTop = if (mPosition == Position.BOTTOM) ARROW_HEIGHT.toFloat() else 0.toFloat()
        val spacingRight = if (mPosition == Position.LEFT) ARROW_HEIGHT.toFloat() else 0.toFloat()
        val spacingBottom = if (mPosition == Position.TOP) ARROW_HEIGHT.toFloat() else 0.toFloat()

        val left: Float = spacingLeft + myRect.left
        val top: Float = spacingTop + myRect.top
        val right: Float = myRect.right - spacingRight
        val bottom: Float = myRect.bottom - spacingBottom
        val centerX = mViewRect!!.centerX() - x

        path.moveTo(left + topLeftCorner / 2f, top)
        //LEFT, TOP

        //LEFT, TOP
        val ARROW_WIDTH = 15
        if (mPosition == Position.BOTTOM) {
            path.lineTo(centerX - ARROW_WIDTH, top)
            path.lineTo(centerX, myRect.top)
            path.lineTo(centerX + ARROW_WIDTH, top)
        }
        path.lineTo(right - topRightCorner / 2f, top)

        path.quadTo(right, top, right, top + topRightCorner / 2)
        //RIGHT, TOP

        //RIGHT, TOP
        if (mPosition == Position.LEFT) {
            path.lineTo(right, bottom / 2f - ARROW_WIDTH)
            path.lineTo(myRect.right, bottom / 2f)
            path.lineTo(right, bottom / 2f + ARROW_WIDTH)
        }
        path.lineTo(right, bottom - bottomRightCorner / 2)

        path.quadTo(right, bottom, right - bottomRightCorner / 2, bottom)
        //RIGHT, BOTTOM

        //RIGHT, BOTTOM
        if (mPosition == Position.TOP) {
            path.lineTo(centerX + ARROW_WIDTH, bottom)
            path.lineTo(centerX, myRect.bottom)
            path.lineTo(centerX - ARROW_WIDTH, bottom)
        }
        path.lineTo(left + bottomLeftCorner / 2, bottom)

        path.quadTo(left, bottom, left, bottom - bottomLeftCorner / 2)
        //LEFT, BOTTOM

        //LEFT, BOTTOM
        if (mPosition == Position.RIGHT) {
            path.lineTo(left, bottom / 2f + ARROW_WIDTH)
            path.lineTo(myRect.left, bottom / 2f)
            path.lineTo(left, bottom / 2f - ARROW_WIDTH)
        }
        path.lineTo(left, top + bottomLeftCorner / 2)

        path.quadTo(left, top, left + bottomLeftCorner / 2, top)

        path.close()

        return path
    }

    /**
     * 显示的位置
     */
    enum class Position {
        LEFT, RIGHT, TOP, BOTTOM
    }

    enum class ALIGN {
        START, CENTER
    }

    interface TooltipAnimation {
        fun animateEnter(view: View, animatorListener: AnimatorListener)
        fun animateExit(view: View, animatorListener: AnimatorListener)
    }

    /**
     * 显示监听
     */
    interface ListenerDisplay {
        fun onDisplay(view: View?)
    }

    /**
     * 隐藏监听
     */
    interface ListenerHide {
        fun onHide(view: View?)
    }
}