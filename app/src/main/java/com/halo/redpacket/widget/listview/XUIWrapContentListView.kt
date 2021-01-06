package com.halo.redpacket.widget.listview

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView
import com.halo.redpacket.R

/**
 * 支持高度值为 wrap_content 的 {@link ListView}，解决原生 {@link ListView} 在设置高度为 wrap_content 时高度计算错误的 bug。
 *
 * @author xuexiang
 * @since 2018/11/14 下午1:08
 */

class XUIWrapContentListView @JvmOverloads constructor(context: Context, private var mMaxHeight: Int = Int.MAX_VALUE shr 2, attrs: AttributeSet?=null, defStyleAttr: Int=0) : ListView(context, attrs, defStyleAttr) {

    init {
        initAttrs(context, attrs)
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.XUIWrapContentListView)
        if (ta != null) {
            mMaxHeight = ta.getDimensionPixelSize(R.styleable.XUIWrapContentListView_wclv_max_height, mMaxHeight)
            ta.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }

    /**
     * 设置最大高度
     *
     * @param maxHeight 最大高度[px]
     */
    fun setMaxHeight(maxHeight: Int) {
        if (mMaxHeight != maxHeight) {
            mMaxHeight = maxHeight
            requestLayout()
        }
    }
}