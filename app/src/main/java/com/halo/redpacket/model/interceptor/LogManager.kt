package com.halo.redpacket.model.interceptor

object LogManager {
    private var mLogProxy: LogProxy? = null

    fun logProxy(logProxy: LogProxy) {
        this.mLogProxy = logProxy
    }

    fun e(tag: String, msg: String) {
        mLogProxy?.e(tag,msg)
    }

    fun w(tag: String, msg: String) {
        mLogProxy?.w(tag,msg)
    }

    fun i(tag: String, msg: String) {
        mLogProxy?.i(tag,msg)
    }

    fun d(tag: String, msg: String?) {
        mLogProxy?.d(tag, msg)
    }
}