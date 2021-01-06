package com.halo.redpacket.ui.fragment.spinner

import android.content.Context
import android.view.View
import com.halo.redpacket.R
import com.halo.redpacket.util.ResUtils
import com.halo.redpacket.widget.listview.BaseListAdapter
import kotlinx.android.synthetic.main.adapter_drop_down_list_item.view.*

class CityDropDownAdapter(context: Context, data: Array<String>? = null) : BaseListAdapter<String, CityDropDownAdapter.ViewHolder>(context, data) {

    class ViewHolder(contentView: View) {
        val mText = contentView.text
    }

    override fun getLayoutId(): Int {
        return R.layout.adapter_drop_down_list_item
    }

    override fun createViewHolder(contentView: View): ViewHolder {
        return ViewHolder(contentView)
    }

    override fun convert(holder: ViewHolder, item: String, position: Int) {
        holder.mText.text = item
        if (getSelectPosition() != -1) {
            if (getSelectPosition() == position) {
                holder.mText.isSelected = true
                holder.mText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ResUtils.getVectorDrawable(holder.mText.context, R.drawable.ic_checked_right), null)
            } else {
                holder.mText.isSelected = false
                holder.mText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
            }
        }
    }
}