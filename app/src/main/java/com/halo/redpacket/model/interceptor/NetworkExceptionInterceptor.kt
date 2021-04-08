package com.halo.redpacket.model.interceptor

import com.halo.redpacket.model.bean.GlobalNetworkException
import com.halo.redpacket.util.RxBus
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.nio.charset.Charset

/***
 * 使用拦截器处理异常
 * 监听异常事件：解析响应 json 中的 code 字段，如果遇到业务异常，同样使用 RxBus 将业务异常的信息给用户进行提示
 * 网络请求返回的 Response 大多是采用如下这种形式：
    {
        "code":0,
        "message":"success",
        "data":{
            ...
        }
    }
 */
class NetworkExceptionInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code() == Config.NETWORK_OK) {
            val responseBody = response.body()
            val source = responseBody!!.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer()
            val bodyString = buffer.clone().readString(Charset.forName("UTF-8"))

            if (bodyString.startsWith("{")) {
                val code = JSONObject(bodyString).getInt("code")
                if (code != Config.NETWORK_RESPONSE_OK) {
                    RxBus.get().post(GlobalNetworkException(code, bodyString))
                }
            }
        }
        return response
    }

}