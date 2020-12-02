package com.halo.redpacket.model

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response

class LoggingInterceptor: Interceptor {
    private val isDebug: Boolean = false
    private lateinit var builder: Builder

    class Builder() {

        val headers: Headers = Headers.of()

        fun loggable(able: Boolean): Builder {
            TODO("Not yet implemented")
        }

        fun request(): Builder {
            TODO("Not yet implemented")
        }

        fun requestTag(tag: String): Builder {
            TODO("Not yet implemented")
        }

        fun response(): Builder {
            TODO("Not yet implemented")
        }

        fun responseTag(tag: String): Builder {
            TODO("Not yet implemented")
        }

        fun build(): LoggingInterceptor {
            TODO("Not yet implemented")
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (builder.headers.size() > 0) {
            val requestBuilder = request.newBuilder()
            requestBuilder.headers(builder.headers)

            val headers = request.headers()
            val iterator = headers.names().iterator()
            while (iterator.hasNext()) {
                val name = iterator.next()
                headers[name]?.let {
                    requestBuilder.addHeader(name, it)
                }
            }
            request = requestBuilder.build()
        }
        if (!isDebug) {
            return chain.proceed(request)
        }
        val body = request.body()
        var rContentType: MediaType? = null
        rContentType = body?.contentType()
        var rSubtype: String? = null
        rSubtype = rContentType?.subtype()

    }
}