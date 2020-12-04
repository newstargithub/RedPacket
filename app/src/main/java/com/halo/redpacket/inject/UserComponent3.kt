package com.halo.redpacket.inject

import com.halo.redpacket.HomeActivity
import dagger.Component

@Component(modules = arrayOf(UserModule3::class))
interface UserComponent3 {
    fun inject(activity: HomeActivity)
}