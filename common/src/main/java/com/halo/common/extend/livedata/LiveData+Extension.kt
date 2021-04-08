package com.halo.redpacket.extend.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.MainThreadDisposable

/**
 * LiveData 的扩展库,
 */
fun <T> LiveData<T>.toObservable(ower: LifecycleOwner? = null) = LiveDataObservable(ower, this)

fun <T> LiveData<T>.toFlowable(owner: LifecycleOwner? = null, strategy: BackpressureStrategy = BackpressureStrategy.LATEST): Flowable<T> {
    return Flowable.create({ emitter ->
        //lifecycle观察者
        val observer = Observer<T> {
            it?.let { emitter.onNext(it) }
        }
        if (owner == null) {
            observeForever(observer)
        } else {
            observe(owner, observer)
        }
        emitter.setCancellable {
            object : MainThreadDisposable() {
                override fun onDispose() = removeObserver(observer)
            }
        }
    }, strategy)
}