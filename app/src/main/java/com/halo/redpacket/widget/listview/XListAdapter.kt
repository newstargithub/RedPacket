package com.halo.redpacket.widget.listview

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.BaseAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * 集合列表适配器
 *
 * @param <T>
 * @author XUE
 */
open abstract class XListAdapter<T> (private val mContext: Context, data: List<T>? = null): BaseAdapter() {

    private val mData = ArrayList<T>()
    /**
     * 当前点击的条目
     */
    private var mSelectPosition: Int = -1

    /**
     * Java List<->数组
     * String[] array = list.toArray(new String[])
     * List<String> list = Arrays.asList(b)
     *
     * Kotlin List<->数组
     * val array = list.toTypedArray()
     * val list = listOf(*array)
     */
    constructor(context: Context, array: Array<T>?): this(context, if(array != null) listOf(*array) else null)

    init {
        setData(data)
    }

    fun setData(data: List<T>?) {
        if (data != null) {
            mData.clear()
            mData.addAll(data)
            mSelectPosition = -1
            notifyDataSetChanged()
        }
    }

    fun setData(array: Array<T>?) {
        if (!array.isNullOrEmpty()) {
            setData(listOf(*array))
        }
    }

    fun addData(data: List<T>?) {
        if (!data.isNullOrEmpty()) {
            mData.addAll(data)
            notifyDataSetChanged()
        }
    }

    fun addData(array: Array<T>?) {
        if (!array.isNullOrEmpty()) {
            mData.addAll(listOf(*array))
            notifyDataSetChanged()
        }
    }

    fun addData(data: T?) {
        if (data != null) {
            mData.add(data)
            notifyDataSetChanged()
        }
    }

    fun removeElement(data: T) {
        if (mData.contains(data)) {
            mData.remove(data)
            notifyDataSetChanged()
        }
    }

    fun removeElement(position: Int) {
        if (position < mData.size) {
            mData.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun removeElements(elements: List<T>?) {
        if (elements != null && elements.size > 0 && mData.size >= elements.size) {
            for (element in elements) {
                if (mData.contains(element)) {
                    mData.remove(element)
                }
            }
            notifyDataSetChanged()
        }
    }

    fun removeElements(elements: Array<T>?) {
        if (elements != null && elements.size > 0) {
            removeElements(Arrays.asList(*elements))
        }
    }

    fun updateElement(element: T, position: Int) {
        if (checkPosition(position)) {
            mData[position] = element
            notifyDataSetChanged()
        }
    }

    private fun checkPosition(position: Int): Boolean {
        return position >= 0 && position < mData.size
    }

    fun clearData() {
        mData.clear()
        mSelectPosition = -1
        notifyDataSetChanged()
    }

    fun clearNotNotify() {
        mData.clear()
        mSelectPosition = -1
    }

    protected open fun visible(flag: Boolean, view: View) {
        if (flag) {
            view.visibility = View.VISIBLE
        }
    }

    protected open fun gone(flag: Boolean, view: View) {
        if (flag) {
            view.visibility = View.GONE
        }
    }

    protected open fun inVisible(view: View) {
        view.visibility = View.INVISIBLE
    }

    protected open fun getDrawable(resId: Int): Drawable? {
        return mContext.getResources().getDrawable(resId)
    }

    protected open fun getString(resId: Int): String? {
        return mContext.getResources().getString(resId)
    }

    protected open fun getColor(resId: Int): Int {
        return mContext.getResources().getColor(resId)
    }

    open fun getItems(): List<T>? {
        return mData
    }

    open fun getSize(): Int {
        return mData.size
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(position: Int): T? {
        return if (checkPosition(position)) mData[position] else null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * @return 当前列表的选中项
     */
    fun getSelectPosition(): Int {
        return mSelectPosition
    }

    /**
     * 设置当前列表的选中项
     *
     * @param selectPosition
     * @return
     */
    fun setSelectPosition(selectPosition: Int): XListAdapter<T> {
        mSelectPosition = selectPosition
        notifyDataSetChanged()
        return this
    }

    /**
     * 获取当前列表选中项
     *
     * @return 当前列表选中项
     */
    fun getSelectItem():T? {
        return getItem(mSelectPosition)
    }

    open fun getContext(): Context {
        return mContext
    }


}