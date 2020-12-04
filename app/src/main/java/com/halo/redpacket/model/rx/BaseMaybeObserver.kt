package com.halo.redpacket.model.rx

import android.accounts.NetworkErrorException
import android.content.Context
import com.halo.redpacket.R
import com.halo.redpacket.model.bean.FailedEvent
import com.halo.redpacket.model.interceptor.LogManager
import com.halo.redpacket.util.AppUtils
import com.halo.redpacket.util.RxBus
import io.reactivex.observers.DisposableMaybeObserver
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseMaybeObserver<T> : DisposableMaybeObserver<T>() {

    internal val mAppContext: Context

    init {
        mAppContext = AppUtils.getApplicationContext()!!
    }

    override fun onSuccess(data: T) {
        onMaybeSuccess(data)
    }

    abstract fun onMaybeSuccess(data: T)

    override fun onError(e: Throwable) {
        var message = e.message
        LogManager.d("log", message)

        when(e) {// 枚举各种网络异常
            is ConnectException -> message = mAppContext.getString(R.string.connect_exception_error)
            is SocketTimeoutException -> message = mAppContext.getString(R.string.timeout_error)
            is UnknownHostException -> message = mAppContext.getString(R.string.network_error)
            is NetworkErrorException -> message = mAppContext.getString(R.string.network_error)
            else -> message = mAppContext.getString(R.string.something_went_wrong)
        }
        RxBus.get().post(FailedEvent(message))
    }

    override fun onComplete() {
    }

}