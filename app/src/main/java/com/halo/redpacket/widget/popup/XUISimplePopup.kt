package com.halo.redpacket.widget.popup

import android.content.Context
import android.widget.AdapterView
import com.halo.redpacket.R
import com.halo.redpacket.util.ResUtils
/**
 * 简单的弹窗
 *
 * @author xuexiang
 * @since 2019/1/14 下午10:07
 */
class XUISimplePopup(context: Context, adapter: XUISimpleAdapter): XUIListPopup(context, mAdapter = adapter) {

    constructor(context: Context, listItems: Array<String>?)
        : this(context, XUISimpleAdapter.create(context, listItems))

    constructor(context: Context, listItems: List<AdapterItem>?):
        this(context, XUISimpleAdapter(context, listItems))

    constructor(context: Context, listItems: Array<AdapterItem>?):
        this(context, XUISimpleAdapter(context, listItems))

    /**
     * 创建弹窗
     *
     * @param maxHeight
     * @return
     */
    fun create(maxHeight: Int): XUISimplePopup {
        create(getPopupWidth(), maxHeight)
        return this
    }

    /**
     * 创建弹窗
     *
     * @param maxHeight
     * @param onItemClickListener
     * @return
     */
    fun create(maxHeight: Int, onItemClickListener: OnPopupItemClickListener?): XUISimplePopup {
        create(getPopupWidth(), maxHeight)
        setOnPopupItemClickListener(onItemClickListener)
        return this
    }

    /**
     * 创建弹窗
     *
     * @param onItemClickListener
     * @return
     */
    fun create(onItemClickListener: OnPopupItemClickListener?): XUISimplePopup {
        create(getPopupWidth())
        setOnPopupItemClickListener(onItemClickListener)
        return this
    }

    /**
     * @return 获取弹出窗的宽度
     */
    private fun getPopupWidth(): Int {
        val width = ResUtils.getDimensionPixelSize(R.dimen.xui_popup_width_phone)
        return width
    }

    /**
     * 设置条目点击监听
     *
     * @param onItemClickListener
     * @return
     */
    fun setOnPopupItemClickListener(onItemClickListener: OnPopupItemClickListener?): XUISimplePopup {
        getListView()?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val item = getAdapter().getItem(position)
            if (item != null) {
                onItemClickListener?.onItemClick(getAdapter(), item, position)
            }
            dismiss()
        }
        return this
    }

    override fun getAdapter(): XUISimpleAdapter {
        return mAdapter as XUISimpleAdapter
    }

    override fun setHasDivider(hasDivider: Boolean): XUISimplePopup {
        super.setHasDivider(hasDivider)
        return this
    }

    /**
     * 条目点击监听
     */
    interface OnPopupItemClickListener {
        /**
         * 条目点击
         *
         * @param adapter
         * @param item
         * @param position
         */
        fun onItemClick(adapter: XUISimpleAdapter, item: AdapterItem, position: Int)
    }


}