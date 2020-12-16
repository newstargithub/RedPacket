package com.halo.redpacket.model

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.halo.redpacket.extend.livedata.bindLifecycle
import com.halo.redpacket.model.bean.BaseResponse
import com.halo.redpacket.util.RxJavaUtils
import com.safframework.lifecycle.RxLifecycle
import io.reactivex.Observable

class LoginViewModel {
    var phoneNumber: LiveData<String>  = MutableLiveData()
    var verificationCode: LiveData<String>  = MutableLiveData()

    fun login(activity: AppCompatActivity): Observable<LoginResponse> {
        val param = LoginParam()
        param.phoneNo = phoneNumber.value.toString()
        param.validationCode = verificationCode.value.toString()

        return RetrofitManager.get()
                .apiService()
                .login(param)
                .compose(RxJavaUtils.observableToMain())
                //添加了一个Fragment来监听声明周期，根据事件决定跳过事件
                .compose(RxLifecycle.bind(activity).toLifecycleTransformer())
    }

    fun login(owner: LifecycleOwner): Observable<LoginResponse> {
        val param = LoginParam()
        param.phoneNo = phoneNumber.value.toString()
        param.validationCode = verificationCode.value.toString()

        return RetrofitManager.get()
                .apiService()
                .login(param)
                .compose(RxJavaUtils.observableToMain())
                .bindLifecycle(owner)
    }
}

class LoginParam {
    var phoneNo: String? = null
    var validationCode: String? = null
}

class LoginData {
    var token: String? = null
}

class LoginResponse(code: Int = -1, message: String? = null, data: LoginData? = null) : BaseResponse<LoginData>(code, message, data)