package com.halo.redpacket.inject

import dagger.Module
import dagger.Provides

/**
 * @Inject 无法对第三方框架中的对象进行注入，也无法对依赖类型为接口的情况进行注入，因为接口不能实例化。
 * 因此，我们需要使用其他的方式来实现注入，Dagger 2 为我们提供了 @Provides 和 @Module。
 *
    @Provides：标注的方法能够提供依赖类/接口的实例，该方法的返回值就是依赖的对象实例。
    @Module：使用 @Provides 方法的类必须用 @Module 进行标注。
 */
@Module
class UserModule2 {
    @Provides
    fun providerUser(): UserInject = UserInject()

}