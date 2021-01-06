package com.halo.redpacket.widget.popup

import android.content.Context
import android.view.Gravity
import android.view.View
import com.halo.redpacket.R
import com.halo.redpacket.extend.gone
import com.halo.redpacket.extend.visible
import com.halo.redpacket.widget.listview.BaseListAdapter

class XUISimpleAdapter(context: Context, list: List<AdapterItem>? = null) : BaseListAdapter<AdapterItem, SimpleItemViewHolder>(context, list) {

    constructor(context: Context, array: Array<AdapterItem>?): this(context, if(array != null) listOf(*array) else null)

    private var mPaddingStartPx: Int = 0

    companion object {
        /**
         * 创建简单的适配器【不含图标】
         *
         * @param context
         * @param data
         * @return
         */
        fun create(context: Context, listItems: Array<String>?): XUISimpleAdapter  {
            if (listItems.isNullOrEmpty()) {
                return XUISimpleAdapter(context)
            } else {
                val list = arrayListOf<AdapterItem>()
                for (text in listItems) {
                    list.add(AdapterItem(text))
                }
                return XUISimpleAdapter(context, list)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.xui_adapter_listview_simple_item
    }

    override fun createViewHolder(contentView: View): SimpleItemViewHolder {
        val viewHolder = SimpleItemViewHolder()
        viewHolder.mLLContentView = contentView.findViewById(R.id.ll_content)
        viewHolder.mTvTitle = contentView.findViewById(R.id.tv_title)
        viewHolder.mIvIcon = contentView.findViewById(R.id.iv_icon)

        if (mPaddingStartPx != 0) {
            viewHolder.mLLContentView.setPaddingRelative(mPaddingStartPx, 0, 0, 0)
            viewHolder.mLLContentView.gravity = Gravity.CENTER_VERTICAL
        } else {
            viewHolder.mLLContentView.gravity = Gravity.CENTER
        }
        return viewHolder
    }

    fun setPaddingStartPx(paddingStartPx: Int): XUISimpleAdapter {
        mPaddingStartPx = paddingStartPx
        return this
    }

    override fun convert(holder: SimpleItemViewHolder, item: AdapterItem, position: Int) {
        holder.mTvTitle.text = item.getTitle()
        if (item.getIcon() != null) {
            holder.mIvIcon.setImageDrawable(item.getIcon())
            holder.mIvIcon.visible()
        } else {
            holder.mIvIcon.gone()
        }
    }
}