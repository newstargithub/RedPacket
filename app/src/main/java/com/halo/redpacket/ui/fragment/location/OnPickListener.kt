package com.halo.redpacket.ui.fragment.location

import com.halo.redpacket.ui.fragment.location.model.City

/**
 * 城市选择监听
 *
 * @author xuexiang
 * @since 2018/12/30 下午6:46
 */
interface OnPickListener {

    /**
     * 选择
     *
     * @param position
     * @param city
     */
    fun onPick(position: Int, city: City)

    /**
     * 取消
     */
    fun onCancel()

    /**
     * 定位
     *
     * @param listener
     */
    fun onLocate(listener: OnLocationListener)
}