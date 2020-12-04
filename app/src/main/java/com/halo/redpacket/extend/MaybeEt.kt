package com.halo.redpacket.extend

import io.reactivex.Maybe

/**
 * 尝试重试
 * ﻿默认有3次重试机会，每次的延迟时间是1000ms
 */
fun <T> Maybe<T>.retryWithDelayMillis(maxReties: Int = 3, retryDelayMillis: Int = 1000) : Maybe<T> {
    return this.retryWithDelayMillis(maxReties, retryDelayMillis)
}

/**
 * 遇到错误时，能够提前捕获异常，并发射一个默认的值。
 * 后面无须再做异常处理
 */
fun <T> Maybe<T>.errorReturn(defaultValue: T) : Maybe<T> {
    return this.onErrorReturn {
        it.printStackTrace()
        return@onErrorReturn defaultValue
    }
}

/**
 * action 参数传递的是一个函数，专门用于处理异常。
 */
fun <T> Maybe<T>.errorReturn(defaultValue: T, action: (Throwable)-> Unit) : Maybe<T> {
    return this.onErrorReturn {
        action.invoke(it)
        return@onErrorReturn defaultValue
    }
}

/**
 * 遇到错误时，能够提前捕获异常，并返回一个新的Maybe
 * 后面无须再做异常处理
 */
fun <T> Maybe<T>.errorResumeNext(defaultValue: T) : Maybe<T> = this.onErrorResumeNext(Maybe.just(defaultValue))

fun <T> Maybe<T>.errorResumeNext():Maybe<T> = this.onErrorResumeNext(Maybe.empty())