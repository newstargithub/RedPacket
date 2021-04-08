package com.halo.redpacket.model.interceptor

import android.util.Log


/**
 * 在使用日志拦截器之前
 * 必须要先实现 LogProxy ，否则无法打印网络请求的 request 、response
 * 所以，先调用这个方法
 */
fun init() {
    LogManager.logProxy(object : LogProxy {
        override fun e(tag: String, msg: String) {
            Log.e(tag,msg)
        }

        override fun w(tag: String, msg: String) {
            Log.w(tag,msg)
        }

        override fun i(tag: String, msg: String) {
            Log.i(tag,msg)
        }

        override fun d(tag: String, msg: String?) {
            TODO("Not yet implemented")
        }
    })
}

class Config {
    companion object {
        val NETWORK_OK = 200
        val NETWORK_RESPONSE_OK = 0
    }
}
