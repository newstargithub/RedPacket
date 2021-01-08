package com.halo.redpacket.ui.fragment.location.model

import android.text.TextUtils
import java.util.regex.Pattern

/**
 * 城市信息
 *
 * @author xuexiang
 * @since 2018/12/30 下午6:26
 */
open class City(val name: String, val province: String, val pinyin: String, val code: String) {

    /***
     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
     * @return
     */
    fun getSection(): String {
        if (TextUtils.isEmpty(pinyin)) {
            return "#"
        } else {
            val c = pinyin.substring(0, 1)
            val pattern = Pattern.compile("[a-zA-z]")
            val matcher = pattern.matcher(c)
            if (matcher.matches()) {
                return c.toUpperCase()
            } else {
                //在添加定位和热门数据时设置的section就是‘定’、’热‘开头
                if (TextUtils.equals(c, "定") || TextUtils.equals(c, "热")) {
                    return pinyin
                } else {
                    return "#"
                }
            }
        }
    }
}