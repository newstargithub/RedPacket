/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.halo.redpacket.ui.fragment.spinner

import android.content.Context
import android.view.View
import android.widget.TextView
import com.halo.redpacket.R
import com.halo.redpacket.widget.listview.BaseListAdapter
import kotlinx.android.synthetic.main.adapter_drop_down_list_item.view.*

class ListDropDownAdapter(context: Context, data: Array<String>?) : BaseListAdapter<String, ListDropDownAdapter.ViewHolder>(context, data) {

    override fun getLayoutId(): Int = R.layout.adapter_drop_down_list_item

    override fun createViewHolder(contentView: View): ViewHolder {
        return ViewHolder(contentView)
    }

    override fun convert(holder: ViewHolder, item: String, position: Int) {
        holder.mText.text = item
        if (getSelectPosition() != -1) {
            if (getSelectPosition() == position) {
                holder.mText.isSelected = true
                holder.mText.setBackgroundResource(R.color.check_bg)
            } else {
                holder.mText.isSelected = false
                holder.mText.setBackgroundResource(R.color.white)
            }
        }
    }

    class ViewHolder(view: View) {
        var mText: TextView = view.text
    }

}