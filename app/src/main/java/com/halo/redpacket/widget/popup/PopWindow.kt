package com.halo.redpacket.widget.popup

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.LayoutRes
import com.halo.redpacket.R

/**
 * @param contentView
 * @param width 宽
 * @param height 高
 */
class PopWindow(contentView: View, width: Int, height: Int) : PopupWindow(contentView, width, height, false) {

    private var mPopupWidth: Int = 0
    private var mPopupHeight: Int = 0

    /**
     * @param contentView 布局控件
     */
    constructor(contentView: View): this(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    /**
     * @param context
     * @param layoutId 布局资源id
     * @param width 宽
     * @param height 高
     */
    constructor(context: Context, @LayoutRes layoutId: Int, width: Int, height: Int)
            : this(LayoutInflater.from(context).inflate(layoutId, null), width, height)

    /**
     * @param context
     * @param layoutId 布局资源id
     */
    constructor(context: Context, @LayoutRes layoutId: Int)
            : this(LayoutInflater.from(context).inflate(layoutId, null), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    init {
        init(contentView.context)
    }

    /**
     * 默认可聚焦、可外部点击消失、无背景
     */
    private fun init(context: Context) {
        isFocusable = true
        isOutsideTouchable = true
        // 必须设置，否则获得焦点后页面上其他地方点击无响应
        setBackgroundDrawable(context.resources.getDrawable(R.drawable.xui_bg_center_popwindow))
        measurePopWindowSize()
    }

    /**
     * 计算popwindow的尺寸
     */
    private fun measurePopWindowSize() {
        //获取自身的长宽高
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        mPopupWidth = contentView.measuredWidth
        mPopupHeight = contentView.measuredHeight
    }

    /**
     * 点击显示或者隐藏弹窗
     * @param v 点击显示弹窗的控件
     */
    fun onClick(v: View) {
        if (isShowing) {
            dismiss()
        } else {
            showAsDropDown(v)
        }
    }

    /**
     * 点击显示或者隐藏弹窗
     * @param v 点击显示弹窗的控件
     * @param xoff x轴偏移量
     * @param yoff y轴偏移量
     */
    fun  onClick(v: View, xoff: Int, yoff: Int) {
        if (isShowing) {
            dismiss()
        } else {
            showAsDropDown(v, xoff, yoff)
        }
    }

    /**
     * 点击显示或者隐藏弹窗（以v的中心位置为开始位置）
     * @param v 点击显示弹窗的控件
     */
    fun onClickUp(v: View) {
        if (isShowing) {
            dismiss()
        } else {
            showUp(v)
        }
    }

    /**
     * 设置显示在v上方（以v的中心位置为开始位置）
     * @param v
     */
    private fun showUp(v: View) {
        //获取需要在其上方显示的控件的位置信息
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, location[0] + v.width / 2 - mPopupWidth / 2, location[1] - v.height / 2 - mPopupHeight)
    }

    /**
     * 点击显示或者隐藏弹窗[显示在v上方(以v的左边距为开始位置)]
     * @param v 点击显示弹窗的控件
     */
    fun onClickUp2(v: View) {
        if (isShowing) {
            dismiss()
        } else {
            showUp2(v)
        }
    }

    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     * @param v
     */
    private fun showUp2(v: View) {
        //获取需要在其上方显示的控件的位置信息
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, location[0] - mPopupWidth / 2, location[1] - mPopupHeight)
    }

    protected fun <T : View?> findViewById(resId: Int): T {
        return contentView.findViewById<T>(resId)
    }

    protected fun getContext(): Context {
        return contentView.context
    }


    companion object {
        /**
         * 隐藏PopWindow
         * @param popWindow
         */
        fun dismissPopWindow(popWindow: PopWindow?) {
            popWindow?.dismiss()
        }
    }

}