package com.halo.redpacket.model.api

import com.halo.redpacket.model.bean.PM25Model
import com.halo.redpacket.model.bean.SO2Model
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Query

interface PmApiService {
    companion object {
        val API_BASE_SERVER_URL = "http://www.pm25.in/"
    }

    @GET("http://www.pm25.in/api/querys/pm2_5.json")
    fun pm25(@Query("city") cityId: String, @Query("token") token: String): Maybe<List<PM25Model>>

    @GET("http://www.pm25.in/api/querys/so2.json")
    fun so2(@Query("city") cityId: String, @Query("token") token: String): Maybe<List<SO2Model>>
}