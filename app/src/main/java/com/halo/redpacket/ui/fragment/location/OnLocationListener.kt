package com.halo.redpacket.ui.fragment.location

import com.halo.redpacket.ui.fragment.location.model.LocatedCity

/**
 * 定位监听
 *
 * @author xuexiang
 * @since 2018/12/30 下午8:53
 */
interface OnLocationListener {

    /**
     * 定位发送变化
     *
     * @param location
     * @param state
     */
    fun onLocationChanged(location: LocatedCity, state: Int)
}