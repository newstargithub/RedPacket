package com.halo.redpacket.ui.fragment.dialog

import androidx.fragment.app.Fragment
import com.halo.redpacket.R
import java.util.ArrayList

/**
 * 简单的页面容器fragment，只需继承PageContainerListFragment， 重写getPagesClasses方法，把需要显示的页面的类传入即可。
 *
 */
abstract class PageContainerListFragment : BaseSimpleListFragment(){

    /**
     * 条目点击
     *
     * @param position
     */
    override fun onItemClick(position: Int) {
        openPage(getSimpleDataItem(position))
    }

    private fun openPage(simpleDataItem: String) {
        activity?.run {
            val clazz = Class.forName(simpleDataItem)
            val fragment = clazz.newInstance()
            if (fragment is Fragment) {
                val fragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.fl_container, fragment).commit()
            }
        }
    }

    /**
     * 初始化页面容器内容
     *
     * @param lists
     * @return
     */
    override fun initSimpleData(list: ArrayList<String>): ArrayList<String> {
        return getSimplePageNames(getSimplePageClasses())
    }

    override fun getSimplePageClasses(): Array<Class<*>>? {
        return getPagesClasses()
    }

    /**
     * 获取页面的类集合[使用@Page注解进行注册的页面]
     *
     * @return
     */
    abstract fun getPagesClasses(): Array<Class<*>>?

    private fun getSimplePageNames(pageClasses: Array<Class<*>>?): ArrayList<String> {
        val simplePageList = ArrayList<String>()
        if (pageClasses != null && pageClasses.isNotEmpty()) {
            for (clazz in pageClasses) {
                simplePageList.add(clazz.name)
            }
        }
        return simplePageList
    }
}