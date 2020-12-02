package com.halo.redpacket.mvvm

import android.arch.lifecycle.ViewModel
import com.halo.redpacket.kt.HttpResponse
import io.reactivex.Observable

/**
 * Author: zx
 * Date: 2020/11/30
 * Description:
 */
class CheckVersionVM : ViewModel() {

    /**
     * 检查版本
     */
    fun getVersion(): Observable<HttpResponse<Any>> {
        return Observable.create {
            it.onNext(HttpResponse(0, "OK", null))
            it.onComplete()
        }
    }
}