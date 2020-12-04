package com.halo.redpacket.inject

import com.halo.redpacket.PmActivity
import dagger.Component

@Component(modules = arrayOf(UserModule2::class, DemoModule::class))
interface UserComponent2 {
    fun inject(activity: PmActivity)
}