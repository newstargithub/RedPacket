package com.halo.redpacket.mvvm

import androidx.lifecycle.ViewModel
import com.halo.redpacket.ktdemo.HttpResponse
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