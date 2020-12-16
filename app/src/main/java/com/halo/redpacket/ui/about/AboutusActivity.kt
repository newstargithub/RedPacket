package com.halo.redpacket.ui.about

import android.os.Bundle
import com.halo.redpacket.R
import com.halo.redpacket.base.BaseActivity
import com.halo.redpacket.mvvm.viewModelDelegate

class AboutusActivity: BaseActivity() {
    private val viewModel by viewModelDelegate(AboutUsViewModel::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        initView()
        initData()
    }

    private fun initData() {

    }

    private fun initView() {
        viewModel.getVersion(this)
                .subscribe({

                }, {
                    it.printStackTrace()
                })
    }

}