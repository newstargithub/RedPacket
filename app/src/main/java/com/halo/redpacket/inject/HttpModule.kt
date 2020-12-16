package com.halo.redpacket.inject

import com.google.gson.GsonBuilder
import com.halo.redpacket.BuildConfig
import com.halo.redpacket.model.GsonStringNullAdapter
import com.halo.redpacket.model.api.APIService
import com.halo.redpacket.model.interceptor.LoggingInterceptor
import com.halo.redpacket.model.interceptor.NetworkExceptionInterceptor
import com.halo.redpacket.util.CertifyUtils
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class HttpModule {

    @Singleton
    @Provides
    internal fun provideRetrofitBuilder() = Retrofit.Builder()

    @Singleton
    @Provides
    internal fun provideAPIService(retrofit: Retrofit): APIService = retrofit.create(APIService::class.java)

    @Singleton
    @Provides
    internal fun provideOkHttpBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()

    /**
     * provideClient() 也有一个参数：builder，它是 OkHttpClient.Builder 类型。
     */
    @Singleton
    @Provides
    internal fun provideClient(builder: OkHttpClient.Builder):  OkHttpClient {
        val client = builder
                .writeTimeout((6 * 1000).toLong(), TimeUnit.MILLISECONDS)
                .readTimeout((6 * 1000).toLong(), TimeUnit.MILLISECONDS)
                .connectTimeout((6 * 1000).toLong(), TimeUnit.MILLISECONDS)
                //设置拦截器
//                .addInterceptor(HeaderInterceptor())
//                .addInterceptor(SSOInterceptor())
                .addInterceptor(NetworkExceptionInterceptor())
                .addInterceptor(loggingInterceptor)
                .build()

        return CertifyUtils.getSSLClientIgnoreExpire(client)
    }

    /**
     * provideRetrofit() 方法包含了2个参数：builder 和 client，分别是 Retrofit.Builder 类型和 OkHttpClient 类型。
     */
    @Singleton
    @Provides
    internal fun provideRetrofit(builder: Retrofit.Builder, okhttpClient: OkHttpClient) : Retrofit {
        return builder.baseUrl(APIService.BASE_URL)
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                        .registerTypeAdapter(String::class.java, GsonStringNullAdapter()) //添加 gson null值String的处理，如果是null，将值改为""
                        .create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
    }

    private val loggingInterceptor = LoggingInterceptor.Builder()
            .loggable(BuildConfig.DEBUG)
            .hideVerticalLine()
            .request()
            .requestTag("Request")
            .response()
            .responseTag("Response")
            .build()
}