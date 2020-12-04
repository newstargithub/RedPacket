package com.halo.redpacket.inject

import dagger.Module
import dagger.Provides

/**
 * 定义一个 Module 类，因为要注入的类型是接口，所以 provideDemoService() 方法必须返回接口类型，否则会编译报错。
 */
@Module
class DemoModule {

    @Provides
    fun providerDemoService(): IService {
        return DemoService()
    }
}