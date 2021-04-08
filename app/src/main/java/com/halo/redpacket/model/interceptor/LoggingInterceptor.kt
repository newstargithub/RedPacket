package com.halo.redpacket.model.interceptor

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class LoggingInterceptor(val builder: Builder): Interceptor {

    private val isDebug: Boolean
    private val charsetUtf8: Charset
    private val excludeList:MutableList<String>  // 排除的 path 列表

    init {
        this.isDebug = builder.isDebug
        this.charsetUtf8 = Charset.forName("UTF-8")
        this.excludeList = builder.excludeList
    }

    /**
     * 嵌套类
     */
    enum class LogLevel {
        ERROR, WARN, INFO, DEBUG
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
            //非debug 模式
            return chain.proceed(request)
        }
        val body = request.body()
        var rContentType: MediaType? = body?.contentType()
        var rSubtype: String? = rContentType?.subtype()

        if (builder.requestFlag) {
            if (request.method() == "GET") {
                Logger.printJsonRequest(builder, request)
            } else {
                if (subtypeIsNotFile(rSubtype)) {
                    Logger.printJsonRequest(builder, request)
                } else {
                    Logger.printFileRequest(builder, request)
                }
            }
        }

        val startTime = System.nanoTime()
        val response = chain.proceed(request)
        if (builder.responseFlag) {
            val requestUrl = request.url()
            val chainMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
            val header = response.headers().toString()
            val code = response.code()
            val isSuccessful = response.isSuccessful
            val responseBody = response.body()
            val contentType = responseBody?.contentType()

            var subtype: String? = contentType?.subtype()
            if (subtypeIsNotFile(subtype)) {
                responseBody?.let {
                    val source = it.source()
                    source.request(Long.MAX_VALUE)
                    val buffer = source.buffer()
                    val bodyString = Logger.getJsonString(buffer.clone().readString(charsetUtf8))
                    Logger.printJsonResponse(builder, chainMs, isSuccessful, code, header, bodyString, requestUrl)
                }
            } else {
                Logger.printFileResponse(builder, chainMs, isSuccessful, code, header, requestUrl)
            }
        }

        return response
    }

    /**
     * 不是文件
     */
    private fun subtypeIsNotFile(subtype: String?): Boolean {
        return subtype != null && (subtype.contains("html")
                || subtype.contains("json")
                || subtype.contains("xml")
                || subtype.contains("plain"))
    }

    class Builder {

        var TAG = "Logging_Interceptor"

        var isDebug: Boolean = false
        var enableThreadName: Boolean = true
        var requestFlag: Boolean = false
        var responseFlag: Boolean = false
        var androidFlag: Boolean = false
        var hideVerticalLineFlag: Boolean = false
        var logLevel: LogLevel = LogLevel.INFO
        var urlLength:Int = 128
        var lineLength:Int = 128
        val excludeList = mutableListOf<String>()

        private var requestTag: String?=null
        private var responseTag: String?=null
        private val builder: Headers.Builder

        init {
            builder = Headers.Builder()
        }

        internal val headers: Headers
            get() = builder.build()

        internal fun getTag(isRequest: Boolean): String {
            return if (isRequest) {
                if (requestTag.isNullOrBlank()) TAG else requestTag!!
            } else {
                if (responseTag.isNullOrBlank()) TAG else responseTag!!
            }
        }

        /**
         * 添加到 header
         * @param name  Filed
         * @param value Value
         * @return Builder
         */
        fun addHeader(name: String, value: String): Builder {
            builder[name] = value
            return this
        }

        /**
         * 设置 request、response 统一的 tag
         * @param tag general log tag
         * @return Builder
         */
        fun tag(tag: String): Builder {
            TAG = tag
            return this
        }

        /**
         * 设置 request 单独的 tag
         * @param tag request log tag
         * @return Builder
         */
        fun requestTag(tag: String): Builder {
            this.requestTag = tag
            return this
        }

        /**
         * 设置 response 单独的 tag
         * @param tag response log tag
         * @return Builder
         */
        fun responseTag(tag: String): Builder {
            this.responseTag = tag
            return this
        }

        /**
         * 设置需要打印 request 的日志
         * Set request log flag
         *
         * @return Builder
         */
        fun request(): Builder {
            this.requestFlag = true
            return this
        }

        /**
         * 设置需要打印 response 的日志
         * Set response log flag
         *
         * @return Builder
         */
        fun response(): Builder {
            this.responseFlag = true
            return this
        }

        /**
         * 设置隐藏竖线边框，便于拷贝日志
         * Set hide vertical line flag
         *
         * @return Builder
         */
        fun hideVerticalLine(): Builder {
            this.hideVerticalLineFlag = true
            return this
        }

        /**
         * 设置日志级别
         *
         * @return Builder
         */
        fun logLevel(logLevel: LogLevel): Builder {
            this.logLevel = logLevel
            return this
        }

        /**
         * 是否打印 request、response 的日志
         * @param isDebug set can sending log output
         *
         * @return Builder
         */
        fun loggable(isDebug: Boolean): Builder {
            this.isDebug = isDebug
            return this
        }

        /**
         * 是否打印线程名，默认会打印
         * @param enable print thread name, default = true
         *
         * @return Builder
         */
        fun printThreadName(enable: Boolean): Builder {
            this.enableThreadName = enable
            return this
        }

        /**
         * 设置需要打印 url 的长度，超过该长度会换行显示
         * @param urlLength
         *
         * @return Builder
         */
        fun urlLength(urlLength:Int):Builder {
            this.urlLength = urlLength
            return this
        }

        fun lineLength(lineLength:Int):Builder {
            this.lineLength = lineLength
            return this
        }

        /**
         * 设置使用的平台是 Android 平台
         *
         * @return Builder
         */
        fun androidPlatform():Builder {
            this.androidFlag = true
            return this
        }

        /**
         * 排除针对某个 path 进行打印日志，
         * 可以不断添加 path
         */
        fun excludePath(path:String): Builder {
            this.excludeList.add(path)
            return this
        }

        fun build() =  LoggingInterceptor(this)
    }
}

