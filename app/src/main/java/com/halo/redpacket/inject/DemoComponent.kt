package com.halo.redpacket.inject

import com.halo.redpacket.HomeActivity
import com.halo.redpacket.PmActivity
import dagger.Component

/**
 * 定义一个 Component 接口，并标明所使用的 DemoModule 类
 */
@Component(modules = arrayOf(DemoModule::class))
interface DemoComponent {
        fun inject(activity: HomeActivity)

}