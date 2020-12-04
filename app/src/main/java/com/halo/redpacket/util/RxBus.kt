package com.halo.redpacket.util

import com.halo.redpacket.util.RxBus.RxBusHolder.mInstance
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxBus {

    private val subjects = PublishSubject.create<Any>()

    internal object RxBusHolder {
        val mInstance = RxBus()
    }

    companion object {
        fun get(): RxBus = mInstance
    }

    /**
     * 发布消息
     * @param event
     */
    fun post(event: Any) {
        subjects.onNext(event)
    }

    /**
     * 订阅消息
     * @param eventType 事件类Class
     * @param <T>
     * @return
    </T> */
    fun <T> tObservable(eventType: Class<T>?): Observable<T>? {
        return subjects.ofType(eventType)
    }


}