package com.halo.redpacket.ui.fragment.dialog

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.halo.redpacket.R
import com.halo.redpacket.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_simple_list.*

/**
 * 简单的ListView演示fragment
 */
abstract class BaseSimpleListFragment: BaseFragment(), AdapterView.OnItemClickListener {

    var mSimpleData = ArrayList<String>()

    override fun layoutId(): Int {
        return R.layout.fragment_simple_list
    }

    override fun initView() {
        listView.onItemClickListener = this
    }

    override fun initData() {
        mSimpleData = initSimpleData(mSimpleData)
        listView.adapter = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, android.R.id.text1, mSimpleData) }
    }

    private fun getListView(): ListView {
        return listView
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onItemClick(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mSimpleData.isNotEmpty()) {
            mSimpleData.clear()
        }
    }

    protected open fun getSimpleDataItem(position: Int): String {
        return mSimpleData[position]
    }

    abstract fun initSimpleData(list: ArrayList<String>): ArrayList<String>

    /**
     * 条目点击
     *
     * @param position 点击的条目索引
     */
    protected abstract fun onItemClick(position: Int)

    /**
     * 获取页面类的集合
     *
     * @return 页面类的集合
     */
    open fun getSimplePageClasses(): Array<Class<*>>? {
        return arrayOf<Class<*>>()
    }
}