package com.halo.redpacket.ui.fragment.location.data

import com.halo.redpacket.ui.fragment.location.model.City

/**
 * 城市中心信息存储
 *
 * @author xuexiang
 * @since 2018/12/30 下午9:47
 */
interface ICityCenter {

    /**
     * 获取所有城市信息
     *
     * @return 所有城市信息的集合
     */
    fun getAllCities(): ArrayList<City>

    /**
     * 搜索城市
     *
     * @param keyword
     * @return 搜索结果
     */
    fun searchCity(keyword: String): ArrayList<City>
}