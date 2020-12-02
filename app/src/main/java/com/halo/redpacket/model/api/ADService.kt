package com.halo.redpacket.model.api

import com.halo.redpacket.model.bean.VwmModel
import com.halo.redpacket.model.bean.VwmParam
import io.reactivex.Maybe
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 多个baseurl
 * 可以在 ADService 的每个接口上标识出该接口完整的路径。
 */
interface ADService {

    @POST("https://xxxx/marketing/vmw/v2")
    fun vmw(@Body param: VwmParam): Maybe<VwmModel>
}