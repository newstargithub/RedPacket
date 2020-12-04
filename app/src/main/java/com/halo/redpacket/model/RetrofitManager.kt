package com.halo.redpacket.model

import com.halo.redpacket.model.api.ADService
import com.halo.redpacket.model.api.APIService
import com.halo.redpacket.model.api.PmApiService
import com.halo.redpacket.model.interceptor.AndroidLoggingInterceptor
import com.halo.redpacket.model.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitManager private constructor(){
    private val apiService: APIService
    private val adService: ADService
    private val pmApiService: PmApiService
    private val okhttpClient: OkHttpClient

    init {
        val logInterceptor = AndroidLoggingInterceptor.build(hideVerticalLine = true)

        //对 OkHttp 进行配置
        val builder = OkHttpClient.Builder()
                //设置拦截器
                .addInterceptor(logInterceptor)
                //设置超时
                .connectTimeout(6000, TimeUnit.MICROSECONDS)
                .readTimeout(6000, TimeUnit.MICROSECONDS)
                .writeTimeout(6000, TimeUnit.MICROSECONDS)

        okhttpClient = builder.build()

        mRetrofit = Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create())//设置 gson 转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())// 设置 RxJava2 适配器
                .build()

        apiService = mRetrofit.create(APIService::class.java)
        adService = mRetrofit.create(ADService::class.java)
        pmApiService = mRetrofit.create(PmApiService::class.java)
    }

    fun retrofit(): Retrofit = mRetrofit

    fun apiService(): APIService = apiService

    // 新增的 adService() 用于调用 ADService 中的接口
    fun adService(): ADService = adService

    fun pmService(): PmApiService = pmApiService

    fun okhttpClient(): OkHttpClient = okhttpClient

    private object Holder {
        val MANAGER = RetrofitManager()
    }

    companion object {
        private lateinit var mRetrofit: Retrofit

        @JvmStatic
        fun get(): RetrofitManager {
            return Holder.MANAGER
        }
    }
}