package com.halo.redpacket.ui.fragment.spinner

import android.content.Context
import android.view.View
import com.halo.redpacket.R
import com.halo.redpacket.widget.listview.BaseListAdapter
import kotlinx.android.synthetic.main.adapter_drop_down_constellation.view.*

class ConstellationAdapter(context: Context, array: Array<String>?) : BaseListAdapter<String, ConstellationAdapter.ViewHolder>(context, array) {

    class ViewHolder(contentView: View) {
        val mText = contentView.text
    }

    override fun getLayoutId(): Int {
        return R.layout.adapter_drop_down_constellation
    }

    override fun createViewHolder(contentView: View): ViewHolder {
        return ViewHolder(contentView)
    }

    override fun convert(holder: ViewHolder, item: String, position: Int) {
        holder.mText.text = item
        if (getSelectPosition() != -1) {
            if (getSelectPosition() == position) {
                holder.mText.setSelected(true)
            } else {
                holder.mText.setSelected(false)
            }
        }
    }
}