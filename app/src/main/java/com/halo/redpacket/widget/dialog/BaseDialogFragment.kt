package com.halo.redpacket.widget.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.halo.redpacket.extend.toast
import com.halo.redpacket.mvvm.BaseViewModel

/**
 * Copyright (C), 2020, iWhaleCloud Co.,Ltd
 * DialogFragment基类
 * @author :        zlh <519009242@qq.com>
 * @date :          2020/11/5 9:20 AM
 * @desc :
 */
abstract class BaseDialogFragment<VM : BaseViewModel?, DB : ViewDataBinding?> : BaseNoModelDialogFragment<DB>() {

    protected var viewModel: VM? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        viewModel = initViewModel()
        return view
    }

    /*
     * 初始化ViewModel
     */
    protected abstract fun initViewModel(): VM

    /**
     * 监听当前ViewModel中 showDialog和error的值
     */
    private fun initObserve() {
        if (viewModel == null) return
        viewModel?.getShowDialog(this, Observer { bean ->
            if (bean.isShow) {
                showDialog(bean.msg)
            } else {
                dismissDialog()
            }
        })
        viewModel?.getError(this, Observer { obj -> showError(obj) })
    }



}