package com.halo.redpacket.ui.fragment

import com.halo.redpacket.R
import com.halo.redpacket.widget.popup.AdapterItem

object DemoProvider {

    var dpiItems = arrayOf(
            "480 × 800",
            "1080 × 1920",
            "720 × 1280")

    var menuItems = arrayOf(
            AdapterItem("登陆", R.drawable.icon_password_login),
            AdapterItem("筛选", R.drawable.icon_filter),
            AdapterItem("设置", R.drawable.icon_setting)
    )


}