package com.halo.redpacket.model.api

import com.halo.redpacket.model.bean.Event
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {

    companion object {
        val BASE_URL = "https://api.github.com/"
    }

    /**
     * @GET 表示该请求的是 Get 请求，@Path 表示 url 的 path 中带有的参数。
     * publicEvent() 返回的类型是 Maybe 类型，当然也可以返回 Observable、Flowable 等类型。
     * 但是网络请求并不是一个连续事件流，我们只会发起一次请求返回数据并且只收到一个事件，所以只需要使用 Maybe 即可。
     */
    @GET("users/{username}/events/public")
    fun publishEvent(@Path("username") userName:String): Maybe<List<Event>>
}