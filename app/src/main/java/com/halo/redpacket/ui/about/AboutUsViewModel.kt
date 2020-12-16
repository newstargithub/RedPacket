package com.halo.redpacket.ui.about

import androidx.lifecycle.LifecycleOwner
import com.halo.redpacket.extend.livedata.bindLifecycle
import com.halo.redpacket.model.bean.HttpResponse
import com.halo.redpacket.model.bean.VersionResponse
import com.halo.redpacket.mvvm.BaseViewModel
import com.halo.redpacket.util.RxJavaUtils
import io.reactivex.Maybe

class AboutUsViewModel: BaseViewModel() {

    fun getVersion(owner: LifecycleOwner): Maybe<HttpResponse<VersionResponse>> =
            apiService.getVersion()
                    .compose(RxJavaUtils.maybeToMain())
                    .bindLifecycle(owner)

}