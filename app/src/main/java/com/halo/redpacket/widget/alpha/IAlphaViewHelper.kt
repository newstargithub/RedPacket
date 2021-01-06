package com.halo.redpacket.widget.alpha

import android.view.View

/**
 * 透明度辅助工具
 *
 * @author xuexiang
 * @since 2018/11/30 下午1:42
 */
interface IAlphaViewHelper {
    /**
     * 处理setPressed
     * @param current
     * @param pressed
     */
    fun onPressedChanged(current: View, pressed: Boolean)

    /**
     * 处理setEnabled
     * @param current
     * @param enabled
     */
    fun onEnabledChanged(current: View?, enabled: Boolean)

    /**
     * 设置是否要在 press 时改变透明度
     *
     * @param changeAlphaWhenPress 是否要在 press 时改变透明度
     */
    fun setChangeAlphaWhenPress(changeAlphaWhenPress: Boolean)

    /**
     * 设置是否要在 disabled 时改变透明度
     *
     * @param changeAlphaWhenDisable 是否要在 disabled 时改变透明度
     */
    fun setChangeAlphaWhenDisable(changeAlphaWhenDisable: Boolean)
}