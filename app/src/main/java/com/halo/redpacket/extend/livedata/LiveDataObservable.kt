package com.halo.redpacket.extend.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

class LiveDataObservable<T>(private val ower: LifecycleOwner?, private val liveData: LiveData<T>): Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>) {
        val liveDataObserver = LiveDataObserver(liveData, observer)
        observer.onSubscribe(liveDataObserver)
        if (ower == null) {
            liveData.observeForever(liveDataObserver)
        } else {
            liveData.observe(ower, liveDataObserver)
        }
    }

    class LiveDataObserver<T>(private val data: LiveData<T>, private val observer: Observer<in T>): MainThreadDisposable(), androidx.lifecycle.Observer<T> {
        override fun onChanged(t: T) {
            if (t!=null) {
                observer.onNext(t)
            }
        }

        override fun onDispose() {
            data.removeObserver(this)
        }
    }
}