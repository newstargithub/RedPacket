package com.halo.redpacket.extend.livedata

import android.annotation.SuppressLint
import android.os.Looper
import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.*
import io.reactivex.android.MainThreadDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Predicate
import org.reactivestreams.Publisher
import java.util.concurrent.CancellationException

object LifecycleConvert {

    @JvmStatic
    fun <T> bindLifecycle(observable: Observable<T>, owner: LifecycleOwner): Observable<T> {
        return observable.compose(bind(owner))
    }

    @JvmStatic
    fun <T> bindLifecycle(flowable: Flowable<T>, owner: LifecycleOwner): Flowable<T> {
        return flowable.compose(bind(owner))
    }

    @JvmStatic
    fun <T> bindLifecycle(single: Single<T>, owner: LifecycleOwner): Maybe<T> = single.toMaybe().compose(bind(owner))

    @JvmStatic
    fun <T> bindLifecycleWithError(single: Single<T>, owner: LifecycleOwner): Single<T> = single.compose(bind(owner))

    @JvmStatic
    fun <T> bindLifecycle(maybe: Maybe<T>, owner: LifecycleOwner): Maybe<T> = maybe.compose(bind(owner))

    @JvmStatic
    fun bindLifecycle(completable: Completable, owner: LifecycleOwner): Completable
            = completable.bindLifecycleWithError(owner).onErrorComplete { it is CancellationException }

    @JvmStatic
    fun bindLifecycleWithError(completable: Completable, owner: LifecycleOwner): Completable
            = completable.compose(bind<Nothing>(owner))

    private fun <T> bind(owner: LifecycleOwner): LifecycleTransformer<T> = LifecycleTransformer(LifecycleObservable(owner))

}

internal class LifecycleObservable(private val owner: LifecycleOwner): Observable<Lifecycle.Event>() {
    override fun subscribeActual(observer: Observer<in Lifecycle.Event>) {
        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            observer.onSubscribe(Disposables.empty())
            observer.onError(IllegalStateException("Expected to be called on the main thread but was " + Thread.currentThread().name))
            return
        }
        val lifecycleObserver = LifecycleObserver(observer)
        observer.onSubscribe(lifecycleObserver)
        owner.lifecycle.addObserver(lifecycleObserver)
    }

    @SuppressLint("RestrictedApi")
    inner class LifecycleObserver(private val observer: Observer<in Lifecycle.Event>) : MainThreadDisposable(), GenericLifecycleObserver {
        override fun onDispose() {
            owner.lifecycle.removeObserver(this)
        }

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (!isDisposed) {
                observer.onNext(event)
            }
        }
    }

    fun checkInActiveState():Boolean = owner.lifecycle.currentState == Lifecycle.State.DESTROYED

    fun checkInActive(event: Lifecycle.Event) = event == Lifecycle.Event.ON_STOP

    fun shouldBeActive(): Boolean = owner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)

    fun isAttachedTo(owner: LifecycleOwner):Boolean = this.owner === owner
}


class LifecycleTransformer<T> internal constructor(
        private val observable: LifecycleObservable
) : ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>,
        CompletableTransformer {

    private val inactive = observable.filter { observable.checkInActive(it) }

    private val active get() = observable.shouldBeActive()

    override fun apply(upstream: Observable<T>): ObservableSource<T> = upstream.filter { active }.takeUntil(inactive)

    override fun apply(upstream: Flowable<T>): Publisher<T> = upstream.filter { active }.takeUntil(inactive.toFlowable(BackpressureStrategy.LATEST))

    override fun apply(upstream: Single<T>): SingleSource<T> = upstream.filterOrNever(Predicate { active }).takeUntil(inactive.firstOrError())

    override fun apply(upstream: Maybe<T>): MaybeSource<T> = upstream.filter { active }.takeUntil(inactive.firstElement())

    override fun apply(upstream: Completable): CompletableSource {

        val completableAfterActive = upstream.andThen {
            if (active) {
                it.onComplete()
            } else {
                it.onError(CancellationException("complete before active"))
            }
        }
        val completableBeforeInactive = inactive.flatMapCompletable { Completable.error(CancellationException()) }
        return Completable.ambArray(completableAfterActive, completableBeforeInactive)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val that = other as? LifecycleTransformer<*>
        return observable == that?.observable
    }

    override fun hashCode(): Int = observable.hashCode()

    override fun toString(): String = "LifecycleTransformer{observable= $observable }"
}