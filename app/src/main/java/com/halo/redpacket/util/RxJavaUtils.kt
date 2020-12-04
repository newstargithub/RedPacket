package com.halo.redpacket.util

import io.reactivex.FlowableTransformer
import io.reactivex.MaybeTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

        /**
         * 切换到主线程
         *  Transformer，是转换器的意思。Transformer 能够将一个 Observable/Flowable/Single/Completable/Maybe
         *  对象转换成另一个 Observable/Flowable/Single/Completable/Maybe 对象。
         *
         *  为啥这样Transformer
         *  RxJava 提倡链式调用，compose 操作符和 Transformer 组合使用能够防止链式被打破。
         */
        @JvmStatic
        fun <T> observableToMain():ObservableTransformer<T, T> {
            return ObservableTransformer{
                upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }

        @JvmStatic
        fun <T> flowableToMain(): FlowableTransformer<T, T> {
            return FlowableTransformer{
                upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }

        /**
         * 一个工具方法用于切换线程。网络请求时使用io线程，请求成功之后切换回主线程方便进行UI的更新。
         */
        @JvmStatic
        fun <T> maybeToMain(): MaybeTransformer<T, T> {
            return MaybeTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }

        /**
         * 针对key做缓存
         */
        @JvmStatic
        fun <T> toCacheTransformer(key: String): MaybeTransformer<T, T> {
            return MaybeTransformer { upstream ->
                upstream.map { t ->
//                    CacheManager.getInstance().put(key, t as Serializable) //做缓存
                    return@map t
                }
            }
        }
    }

}