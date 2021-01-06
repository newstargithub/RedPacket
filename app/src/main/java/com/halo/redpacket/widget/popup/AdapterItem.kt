package com.halo.redpacket.widget.popup

import android.graphics.drawable.Drawable
import com.halo.redpacket.util.ResUtils

/**
 * 简易的适配项
 *
 * @author xuexiang
 * @since 2019/1/14 下午10:13
 */
class AdapterItem(private val mTitle: CharSequence, private val mIcon: Drawable? = null) {

    constructor(title: CharSequence, drawableId: Int): this(title, ResUtils.getDrawable(drawableId))

    constructor(titleId: Int, drawableId: Int): this(ResUtils.getString(titleId), ResUtils.getDrawable(drawableId))

    fun getIcon(): Drawable? {
        return mIcon
    }

    fun getTitle(): CharSequence {
        return mTitle
    }

    override fun toString(): String {
        return mTitle.toString()
    }
}