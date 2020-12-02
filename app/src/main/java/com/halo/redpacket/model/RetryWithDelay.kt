package com.halo.redpacket.model

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.functions.Function
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit

/**
 * 重试机制 跟retryWhen操作符搭配使用
 * Created by tony on 2017/11/6.
 */
class RetryWithDelay(private val maxRetries: Int, private val retryDelayMillis: Long) : Function<Flowable<out Throwable>, Publisher<*>> {

    private var retryCount = 0

    init {
        retryCount = 0
    }

    override fun apply(attempts: Flowable<out Throwable>): Publisher<*> {
        return attempts.flatMap {throwable ->
            if (retryCount++ <= maxRetries) {
                Log.i("RetryWithDelay", "get error, it will try after " + retryDelayMillis
                        + " millisecond, retry count " + retryCount)
                // When this Observable calls onNext, the original
                // Observable will be retried (i.e. re-subscribed).
                Flowable.timer(retryDelayMillis, TimeUnit.MICROSECONDS)
            } else {
                // Max retries hit. Just pass the error along.
                Flowable.error(throwable)
            }
        }
    }

}