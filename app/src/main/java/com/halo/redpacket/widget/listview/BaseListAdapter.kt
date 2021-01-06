package com.halo.redpacket.widget.listview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * 简单的集合列表适配器
 * @author XUE
 *
 * @param <T> 数据类型
 * @param <H> ViewHolder类型
 */
open abstract class BaseListAdapter<T, H>(context: Context, data: List<T>? = null): XListAdapter<T>(context, data) {

    constructor(context: Context, array: Array<T>?): this(context, if(array != null) listOf(*array) else null)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        var holder: H
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(getLayoutId(), parent, false)
            holder = createViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as H
        }
        val item = getItem(position)
        if (item != null) {
            convert(holder, item, position)
        }
        return view
    }

    /**
     * 获取适配的布局ID
     * @return
     */
    abstract fun getLayoutId(): Int

    /**
     * 创建ViewHolder
     * @param convertView
     * @return
     */
    abstract fun createViewHolder(contentView: View): H

    /**
     * 转换布局
     * @param holder
     * @param item
     * @param position
     */
    abstract fun convert(holder: H, item: T, position: Int)
}