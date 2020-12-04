package com.halo.redpacket.model.interceptor

object AndroidLoggingInterceptor {

    @JvmOverloads
    @JvmStatic
    fun build(isDebug: Boolean = true, hideVerticalLine: Boolean = false, requestTag: String = "Request", responseTag: String = "Response"): LoggingInterceptor {
        init()
        val builder = LoggingInterceptor.Builder()
                .loggable(isDebug) // TODO: 发布到生产环境需要改成false
                .androidPlatform()
                .request()
                .requestTag(requestTag)
                .response()
                .responseTag(responseTag)
        if (hideVerticalLine) {
            builder.hideVerticalLine()// 隐藏竖线边框
        }
        return builder.build()
    }
}