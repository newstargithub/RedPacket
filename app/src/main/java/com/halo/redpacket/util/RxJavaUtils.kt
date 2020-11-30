package com.halo.redpacket.util

import io.reactivex.ObservableTransformer
import java.util.concurrent.TimeUnit

class RxJavaUtils {

    companion object {
        /**
         * 防止重复点击的Transformer
         * @JvmStatic 表示方法为静态方法
         * @JvmOverloads：由于使用了默认参数之后，可以避免重载。但是 Java 却无法调用，
         * 因为对 Java 而言只会对一个方法可见，它是所有参数都存在的完整参数签名的方法。
         * 如果希望也向 Java 调用者暴露多个重载，可以使用 @JvmOverloads 注解。
         */
        @JvmOverloads
        @JvmStatic
        fun <T> preventDuplicateClickTransformer(windowDuration: Long = 1000, unit: TimeUnit = TimeUnit.SECONDS): ObservableTransformer<T, T> {
            return ObservableTransformer{
                it.throttleWithTimeout(windowDuration, unit)
            }
        }
    }

}