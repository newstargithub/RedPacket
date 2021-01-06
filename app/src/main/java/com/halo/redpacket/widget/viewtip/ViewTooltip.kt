package com.halo.redpacket.widget.viewtip

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

/**
 * 控件提示弹出窗，可自定义弹出的位置，持续时间以及样式
 *
 * @author xuexiang
 * @since 2019/1/14 上午11:23
 */
class ViewTooltip(private val mView: View) {

    private val mTooltipView: TooltipView

    companion object {
        /**
         * 创建并设置提示控件依附的View
         * @param view
         * @return
         */
        fun on(view: View): ViewTooltip {
            return ViewTooltip(view)
        }
    }

    init {
        mTooltipView = TooltipView(getActivityContext(mView.context))
    }

    private fun getActivityContext(context: Context): Context {
        var c: Context = context
        while (c is ContextWrapper) {
            if (c is Activity) {
                return context
            }
            c = c.baseContext
        }
        return context
    }

    /**
     * 显示
     * @return
     */
    fun show(): ViewTooltip {
        val activityContext = mTooltipView.context
        if (activityContext is Activity) {
            val decorView: ViewGroup = activityContext.window.decorView as ViewGroup
            mView.postDelayed({
                val rect = Rect()
                mView.getGlobalVisibleRect(rect)

                val location = IntArray(2)
                mView.getLocationOnScreen(location)
                rect.left = location[0]
                decorView.addView(mTooltipView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                mTooltipView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        mTooltipView.setup(rect, decorView.width)
                        mTooltipView.viewTreeObserver.removeOnPreDrawListener(this)
                        return false
                    }
                })
            }, 100)
        }
        return this
    }

    /**
     * 设置提示显示的相对位置
     * @param position
     * @return
     */
    fun position(position: TooltipView.Position): ViewTooltip {
        mTooltipView.setPosition(position)
        return this
    }

    /**
     * 设置自定义提示布局
     * @param customView
     * @return
     */
    fun customView(customView: View): ViewTooltip {
        mTooltipView.setCustomView(customView)
        return this
    }

    fun customView(viewId: Int): ViewTooltip {
        mTooltipView.setCustomView((mView.context as Activity).findViewById(viewId))
        return this
    }

    fun align(align: TooltipView.ALIGN): ViewTooltip {
        mTooltipView.setAlign(align)
        return this
    }

    fun close() {
        mTooltipView.close()
    }

    /**
     * 设置提示持续的时间
     * @param duration
     * @return
     */
    fun duration(duration: Long): ViewTooltip {
        mTooltipView.setDuration(duration)
        return this
    }

    /**
     * 设置提示框的背景颜色
     * @param color
     * @return
     */
    fun color(color: Int): ViewTooltip {
        mTooltipView.setColor(color)
        return this
    }

    /**
     * 设置显示的监听
     * @param listener
     * @return
     */
    fun onDisplay(listener: TooltipView.ListenerDisplay?): ViewTooltip {
        mTooltipView.setListenerDisplay(listener)
        return this
    }

    /**
     * 设置隐藏的监听
     * @param listener
     * @return
     */
    fun onHide(listener: TooltipView.ListenerHide?): ViewTooltip {
        mTooltipView.setListenerHide(listener)
        return this
    }

    /**
     * 设置间隔的距离
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    fun padding(left: Int, top: Int, right: Int, bottom: Int): ViewTooltip {
        mTooltipView.mPaddingTop = top
        mTooltipView.mPaddingBottom = bottom
        mTooltipView.mPaddingLeft = left
        mTooltipView.mPaddingRight = right
        return this
    }

    /**
     * 设置显示和隐藏的动画
     * @param tooltipAnimation
     * @return
     */
    fun animation(tooltipAnimation: TooltipView.TooltipAnimation?): ViewTooltip {
        mTooltipView.setTooltipAnimation(tooltipAnimation)
        return this
    }

    /**
     * 设置提示的文字
     * @param text
     * @return
     */
    fun text(text: String?): ViewTooltip {
        mTooltipView.setText(text!!)
        return this
    }

    /**
     * 设置圆角的角度
     * @param corner
     * @return
     */
    fun corner(corner: Int): ViewTooltip {
        mTooltipView.setCorner(corner)
        return this
    }

    /**
     * 设置提示文字的颜色
     * @param textColor
     * @return
     */
    fun textColor(textColor: Int): ViewTooltip {
        mTooltipView.setTextColor(textColor)
        return this
    }

    /**
     * 设置提示文字的字体大小
     * @param unit
     * @param textSize
     * @return
     */
    fun textSize(unit: Int, textSize: Float): ViewTooltip {
        mTooltipView.setTextSize(unit, textSize)
        return this
    }

    fun setTextGravity(textGravity: Int): ViewTooltip {
        mTooltipView.setTextGravity(textGravity)
        return this
    }


    /**
     * 设置是否点击隐藏
     * @param clickToHide
     * @return
     */
    fun clickToHide(clickToHide: Boolean): ViewTooltip {
        mTooltipView.setClickToHide(clickToHide)
        return this
    }

    /**
     * 设置是否点击隐藏
     * @param autoHide
     * @param duration
     * @return
     */
    fun autoHide(autoHide: Boolean, duration: Long = 4000): ViewTooltip {
        mTooltipView.setAutoHide(autoHide)
        mTooltipView.setDuration(duration)
        return this
    }

    class FadeTooltipAnimation(private val mFadeDuration: Long = 400): TooltipView.TooltipAnimation {

        override fun animateEnter(view: View, animatorListener: Animator.AnimatorListener) {
            view.alpha = 0f
            view.animate().alpha(1f).setDuration(mFadeDuration).setListener(animatorListener)
        }

        override fun animateExit(view: View, animatorListener: Animator.AnimatorListener) {
            view.animate().alpha(0f).setDuration(mFadeDuration).setListener(animatorListener)
        }
    }

}