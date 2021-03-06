package com.halo.redpacket.extend.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.*

/**
 * RxJava 的 Observable、Flowbale、Completable、Single、Maybe 转为LiveData
 *支持 RxJava 的 Observable、Flowbale、Completable、Single、Maybe 绑定 AAC 的 Lifecycle。
 */
fun <T> Observable<T>.toLiveData(strategy: BackpressureStrategy = BackpressureStrategy.BUFFER) : LiveData<T> {
    return LiveDataReactiveStreams.fromPublisher(this.toFlowable(strategy))
}

fun <T> Flowable<T>.toLiveData() = LiveDataReactiveStreams.fromPublisher(this)

fun <T> Completable.toLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this.toFlowable<T>())

fun <T> Single<T>.toLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this.toFlowable())

fun <T> Maybe<T>.toLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this.toFlowable())

fun <T> Observable<T>.bindLifecycle(owner: LifecycleOwner): Observable<T> = LifecycleConvert.bindLifecycle(this, owner)

fun <T> Flowable<T>.bindLifecycle(owner: LifecycleOwner): Flowable<T> = LifecycleConvert.bindLifecycle(this, owner)

fun Completable.bindLifecycle(owner: LifecycleOwner): Completable = LifecycleConvert.bindLifecycle(this, owner)

fun Completable.bindLifecycleWithError(owner: LifecycleOwner): Completable = LifecycleConvert.bindLifecycleWithError(this, owner)

fun <T> Single<T>.bindLifecycle(owner: LifecycleOwner): Maybe<T> = LifecycleConvert.bindLifecycle(this, owner)

fun <T> Single<T>.bindLifecycleWithError(owner: LifecycleOwner): Single<T> = LifecycleConvert.bindLifecycleWithError(this, owner)

fun <T> Maybe<T>.bindLifecycle(owner: LifecycleOwner): Maybe<T> = LifecycleConvert.bindLifecycle(this, owner)