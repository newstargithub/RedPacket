package com.halo.redpacket.widget.popup

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListAdapter
import android.widget.ListView
import com.halo.redpacket.R
import com.halo.redpacket.extend.dp2px
import com.halo.redpacket.util.ThemeUtils
import com.halo.redpacket.widget.listview.XUIWrapContentListView

/**
 * 继承自 {@link XUIPopup}，在 {@link XUIPopup} 的基础上，支持显示一个列表。
 * 构造方法.
 *
 * @param context Context
 * @param direction 方位
 * @param adapter 列表适配器
 */
open class XUIListPopup @JvmOverloads constructor(context: Context, preferredDirection: Int = DIRECTION_NONE, protected val mAdapter: ListAdapter)
    : XUIPopup(context, preferredDirection) {

    private var mListView: ListView? = null
    private var mHasDivider: Boolean = false

    /**
     * 创建弹窗
     *
     * @param width               弹窗的宽度
     * @param maxHeight           弹窗最大的高度
     * @param onItemClickListener 列表点击的监听
     * @return
     */
    @JvmOverloads
    fun create(width: Int, maxHeight: Int = 0, onItemClickListener: AdapterView.OnItemClickListener? = null): XUIListPopup {
        val margin = getContext().dp2px(5)
        if (maxHeight != 0) {
            mListView = XUIWrapContentListView(getContext(), maxHeight)
            val lp = FrameLayout.LayoutParams(width, maxHeight)
            lp.setMargins(0, margin, 0, margin)
            mListView?.layoutParams = lp
        } else {
            mListView = XUIWrapContentListView(getContext())
            val lp = FrameLayout.LayoutParams(width, FrameLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, margin, 0, margin)
            mListView?.layoutParams = lp
        }
        mListView?.apply {
            setPadding(margin, 0, margin, 0)
            adapter = mAdapter
            this.onItemClickListener = onItemClickListener
            isVerticalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            updateListViewDivider(this)
        }
        setContentView(mListView)
        return this
    }

    /**
     * 设置列表分隔线
     * @param listView 列表
     */
    private fun updateListViewDivider(listView: ListView) {
        if (mHasDivider) {
            listView.divider = ColorDrawable(ThemeUtils.resolveColor(getContext(), R.attr.xui_config_color_separator_light))
            listView.dividerHeight = getContext().dp2px(0.5f)
        } else {
            listView.divider = null
        }
    }

    /**
     * 设置分割线的资源
     *
     * @param divider
     * @return
     */
    fun setDivider(divider: Drawable): XUIListPopup {
        mListView?.divider = divider
        return this
    }

    /**
     * 设置分割线的高度
     *
     * @param dividerHeight
     * @return
     */
    fun setDividerHeight(dividerHeight: Int): XUIListPopup {
        mListView?.dividerHeight = dividerHeight
        return this
    }

    /**
     * 设置是否有分割线
     *
     * @param hasDivider
     * @return
     */
    open fun setHasDivider(hasDivider: Boolean): XUIListPopup {
        mHasDivider = hasDivider
        if (mListView != null) {
            updateListViewDivider(mListView!!)
        }
        return this
    }

    fun getListView(): ListView? {
        return mListView
    }

    open fun getAdapter(): ListAdapter {
        return mAdapter
    }


}