package com.halo.redpacket.widget.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.StyleRes
import com.halo.redpacket.R
import com.halo.redpacket.extend.gone
import com.halo.redpacket.extend.visible
import kotlinx.android.synthetic.main.xui_layout_loading_view.*

/**
 * loading加载框
 *
 * @author xuexiang
 * @since 2019/1/14 下午10:08
 */
class LoadingDialog(context: Context, @StyleRes themeResId: Int? = null,
                    private val tipMessage: String? = null) : BaseDialog(context, themeResId, R.layout.xui_dialog_loading) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(tipMessage)
    }

    fun initView(tipMessage: String? = getString(R.string.xui_tip_loading_message)) {
        setLoadingMsg(tipMessage)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    /**
     * 更新提示信息
     *
     * @param tipMessage
     * @return
     */
    fun setLoadingMsg(tipMessage: String?) {
        if (TextUtils.isEmpty(tipMessage)) {
            tv_tip_message.text = null
            tv_tip_message.gone()
        } else {
            tv_tip_message.text = tipMessage
            tv_tip_message.visible()
        }
    }

    fun setLoadingMsg(resId: Int) {
        setLoadingMsg(getString(resId))
    }

    fun isLoading(): Boolean {
        return isShowing
    }




}