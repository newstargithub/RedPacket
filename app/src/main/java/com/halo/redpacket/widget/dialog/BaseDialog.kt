package com.halo.redpacket.widget.dialog

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDialog
import com.halo.redpacket.R
import com.halo.redpacket.util.ResUtils

open class BaseDialog(context: Context, theme: Int, private val mContentView: View) : AppCompatDialog(context, theme) {

    constructor(context: Context, layoutId: Int) : this(context, R.style.XUIDialog_Custom, LayoutInflater.from(context).inflate(layoutId, null))

    constructor(context: Context, contentView: View) : this(context, R.style.XUIDialog_Custom, contentView)

    constructor(context: Context, theme: Int?, layoutId: Int) : this(context, theme ?: R.style.XUIDialog_Custom, LayoutInflater.from(context).inflate(layoutId, null))

    init {
        this.setContentView(mContentView)
        this.setCanceledOnTouchOutside(true)
    }

    /**
     * 设置弹窗的宽和高
     *
     * @param width
     * @param height
     */
    fun setWindowSize(width: Int, height: Int): BaseDialog {
        // 获取对话框当前的参数值
        val window = window
        window?.let {
            val params = it.attributes
            params.width = width
            params.height = height
            it.attributes = params
        }
        return this
    }

    override fun <T : View?> findViewById(resId: Int): T {
        return mContentView.findViewById<T>(resId)
    }

    fun getString(resId: Int): String {
        return context.getString(resId)
    }

    fun getDrawable(resId: Int): Drawable? {
        return ResUtils.getDrawable(context, resId)
    }

}