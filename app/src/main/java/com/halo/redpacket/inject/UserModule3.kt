package com.halo.redpacket.inject

import dagger.Module
import dagger.Provides

/**
 * 有 provideUser() 之外，还有 param 的注入。
 */
@Module
class UserModule3 {

    lateinit var param: String

    fun param(param: String) {
        this.param = param
    }

    @Provides
    fun providerUser(): UserInject {
        return UserInject(param)
    }
}