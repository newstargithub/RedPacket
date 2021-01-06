package com.halo.redpacket.widget.popup

import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.halo.redpacket.R
import com.halo.redpacket.extend.gone
import com.halo.redpacket.extend.visible
import kotlin.math.max

/**
 * 提供一个浮层，支持自定义浮层的内容，支持在指定 {@link View} 的任一方向旁边展示该浮层，支持自定义浮层出现/消失的动画。
 *
 * @author xuexiang
 * @since 2019/1/14 下午10:00
 */
open class XUIPopup(context: Context, private var mPreferredDirection: Int = DIRECTION_NONE): XUIBasePopup(context) {

    private var mArrowUp: ImageView? = null
    private var mArrowDown: ImageView? = null

    /**
     * 弹出点位置左上角X
     */
    private var mX: Int = -1
    /**
     * 弹出点位置左上角Y
     */
    private var mY: Int = -1
    /**
     * 依附控件的水平中点
     */
    private var mArrowCenter: Int = 0

    /**
     * 计算位置后的偏移x值
     */
    private var mOffsetX = 0

    /**
     * 计算位置后的偏移y值，当浮层在View的上方时使用
     */
    private var mOffsetYWhenTop = 0

    /**
     * 计算位置后的偏移y值，当浮层在View的下方时使用
     */
    private var mOffsetYWhenBottom = 0

    /**
     * 相对依附控件的方位
     */
    private var mDirection: Int

    /**
     * 动画类型
     */
    private var mAnimStyle: Int

    /**
     * 该PopupWindow的View距离屏幕左右的最小距离
     */
    private var mPopupLeftRightMinMargin = 0

    /**
     * 该PopupWindow的View距离屏幕上下的最小距离
     */
    private var mPopupTopBottomMinMargin = 0

    companion object {
        //方向向上
        const val DIRECTION_TOP = 0
        const val DIRECTION_BOTTOM = 1
        const val DIRECTION_NONE = 2

        //动画
        const val ANIM_GROW_FROM_LEFT = 1
        const val ANIM_GROW_FROM_RIGHT = 2
        const val ANIM_GROW_FROM_CENTER = 3
        const val ANIM_AUTO = 4
    }

    init {
        mAnimStyle = ANIM_AUTO
        mDirection = mPreferredDirection
    }

    override fun setContentView(root: View?) {
        //包裹一层布局
        val container = LayoutInflater.from(getContext()).inflate(R.layout.xui_layout_popup, null, false) as FrameLayout
        mArrowDown = container.findViewById(R.id.arrow_down)
        mArrowUp = container.findViewById(R.id.arrow_up)
        val box = container.findViewById<FrameLayout>(R.id.box)
        box.addView(root)
        super.setContentView(container)
    }

    override fun onShow(anchorView: View): Point {
        calculatePosition(anchorView)
        showArrow()
        initAnimationStyle(mScreenSize.x, mArrowCenter)

        var offsetY = 0
        if (mDirection == DIRECTION_TOP) {
            offsetY = mOffsetYWhenTop
        } else if (mDirection == DIRECTION_BOTTOM) {
            offsetY = mOffsetYWhenBottom
        }
        return Point(mX + mOffsetX, mY + offsetY)
    }

    /**
     * init animation style
     *
     * @param screenWidth screen width
     * @param requestedX  distance from left edge
     */
    private fun initAnimationStyle(screenWidth: Int, requestedX: Int) {
        var arrowPos = requestedX
        if (mArrowUp != null) {
            arrowPos -= mArrowUp!!.measuredWidth / 2
        }
        val onTop = mDirection == DIRECTION_TOP
        when(mAnimStyle) {
            ANIM_GROW_FROM_LEFT -> {
                mPopupWindow.animationStyle = if(onTop) R.style.XUI_Animation_PopUpMenu_Left else R.style.XUI_Animation_PopDownMenu_Left
            }
            ANIM_GROW_FROM_RIGHT -> {
                mPopupWindow.animationStyle = if(onTop) R.style.XUI_Animation_PopUpMenu_Right else R.style.XUI_Animation_PopDownMenu_Right
            }
            ANIM_GROW_FROM_CENTER -> {
                mPopupWindow.animationStyle = if(onTop) R.style.XUI_Animation_PopUpMenu_Center else R.style.XUI_Animation_PopDownMenu_Center
            }
            ANIM_AUTO -> {
                if (arrowPos <= screenWidth / 4) {
                    mPopupWindow.animationStyle = if(onTop) R.style.XUI_Animation_PopUpMenu_Left else R.style.XUI_Animation_PopDownMenu_Left
                } else if (arrowPos <= screenWidth * 3 / 4) {
                    mPopupWindow.animationStyle = if(onTop) R.style.XUI_Animation_PopUpMenu_Center else R.style.XUI_Animation_PopDownMenu_Center
                } else {
                    mPopupWindow.animationStyle = if(onTop) R.style.XUI_Animation_PopUpMenu_Right else R.style.XUI_Animation_PopDownMenu_Right
                }
            }
        }
    }

    /**
     * 显示箭头（上/下）
     */
    private fun showArrow() {
        var showArrow: View? = null
        when(mDirection) {
            DIRECTION_TOP -> {
                mArrowUp?.gone()
                mArrowDown?.visible()
                showArrow = mArrowDown
            }
            DIRECTION_BOTTOM -> {
                mArrowUp?.visible()
                mArrowDown?.gone()
                showArrow = mArrowUp
            }
            DIRECTION_NONE -> {
                mArrowUp?.gone()
                mArrowDown?.gone()
            }
        }
        if (showArrow != null) {
            val arrowWidth = showArrow.measuredWidth
            val layoutParams = showArrow.layoutParams as (ViewGroup.MarginLayoutParams)
            layoutParams.leftMargin = mArrowCenter - mX - arrowWidth / 2
        }
    }

    /**
     * 计算弹出点位置（左上角）
     * @param attachedView
     */
    private fun calculatePosition(attachedView: View?) {
        if (attachedView != null) {
            val attachedViewLocation = IntArray(2)
            //在屏幕上的位置
            attachedView.getLocationOnScreen(attachedViewLocation)
            //依附控件的水平中点
            mArrowCenter = attachedViewLocation[0] + attachedView.width / 2
            if (mArrowCenter < mScreenSize.x / 2) {
                //描点在左侧
                mX = max(mArrowCenter - mWindowWidth / 2, mPopupLeftRightMinMargin)
            } else {
                //描点在右侧
                if (mArrowCenter + mWindowWidth / 2 < mScreenSize.x - mPopupLeftRightMinMargin) {
                    mX = mArrowCenter - mWindowWidth / 2
                } else {
                    mX = mScreenSize.x - mPopupLeftRightMinMargin - mWindowWidth
                }
            }
            //实际的方向和期望的方向可能不一致，每次都需要重新
            mDirection = mPreferredDirection
            when(mPreferredDirection) {
                DIRECTION_TOP -> {
                    mY = attachedViewLocation[1] - mWindowHeight
                    if (mY < mPopupTopBottomMinMargin) {
                        //调整为向下
                        mY = attachedViewLocation[1] + attachedView.height
                        mDirection = DIRECTION_BOTTOM
                    }
                }
                DIRECTION_BOTTOM -> {
                    mY = attachedViewLocation[1] + attachedView.height
                    if (mY > mScreenSize.y - mPopupTopBottomMinMargin) {
                        mY = attachedViewLocation[1] - mWindowHeight
                        mDirection = DIRECTION_TOP
                    }
                }
                DIRECTION_NONE -> {
                    // 默认Y值与attachedView的Y值相同
                    mY = attachedViewLocation[1]
                }
            }
        } else {
            //屏幕正中间
            mX = (mScreenSize.x - mWindowWidth) / 2
            mY = (mScreenSize.y - mWindowHeight) / 2
            mDirection = DIRECTION_NONE
        }
    }

    /**
     * 菜单弹出动画
     *
     * @param animStyle 默认是 ANIM_AUTO
     */
    fun setAnimStyle(animStyle: Int) {
        mAnimStyle = animStyle
    }

    fun setPopupLeftRightMinMargin(margin: Int) {
        mPopupLeftRightMinMargin = margin
    }

    fun setPopupTopBottomMinMargin(margin: Int) {
        mPopupTopBottomMinMargin = margin
    }

    /**
     * 设置根据计算得到的位置后的偏移值
     */
    fun setPositionOffsetX(offsetX: Int): XUIPopup {
        mOffsetX = offsetX
        return this
    }

    /**
     * 设置根据计算得到的位置后的偏移值
     *
     * @param offsetYWhenTop mDirection!=DIRECTION_BOTTOM 时的 offsetY
     */
    fun setPositionOffsetYWhenTop(offsetYWhenTop: Int): XUIPopup {
        mOffsetYWhenTop = offsetYWhenTop
        return this
    }

    /**
     * 设置根据计算得到的位置后的偏移值
     *
     * @param offsetYWhenBottom mDirection==DIRECTION_BOTTOM 时的 offsetY
     */
    fun setPositionOffsetYWhenBottom(offsetYWhenBottom: Int): XUIPopup {
        mOffsetYWhenBottom = offsetYWhenBottom
        return this
    }

    /**
     * 设置弹出的方向
     *
     * @param preferredDirection
     * @return
     */
    fun setPreferredDirection(preferredDirection: Int): XUIPopup {
        mPreferredDirection = preferredDirection
        return this
    }

    /**
     * 向上显示
     *
     * @param v
     */
    fun showUp(v: View) {
        setPreferredDirection(DIRECTION_TOP)
        show(v)
    }

    /**
     * 向下显示
     *
     * @param v
     */
    fun showDown(v: View) {
        setPreferredDirection(DIRECTION_BOTTOM)
        show(v)
    }


}