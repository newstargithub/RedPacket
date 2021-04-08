package com.halo.redpacket.ui.about

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.halo.redpacket.extend.livedata.bindLifecycle
import com.halo.redpacket.model.bean.HttpResponse
import com.halo.redpacket.model.bean.VersionResponse
import com.halo.redpacket.mvvm.BaseViewModel
import com.halo.redpacket.util.RxJavaUtils
import com.safframework.lifecycle.RxLifecycle
import io.reactivex.Maybe
class AboutUsViewModel: BaseViewModel() {

    /**
     * 使用Lifecycle
     */
    fun getVersion(owner: LifecycleOwner): Maybe<HttpResponse<VersionResponse>> {
        return apiService.getVersion()
                .compose(RxJavaUtils.maybeToMain())
                .bindLifecycle(owner) //绑定生命周期
    }

    /**
     * 使用RxLifecycle
     */
    fun getVersion2(activity: AppCompatActivity): Maybe<HttpResponse<VersionResponse>> {
        return apiService
                .getVersion()
                .compose(RxJavaUtils.maybeToMain())
                .compose(RxLifecycle.bind(activity).toLifecycleTransformer());
    }


}

