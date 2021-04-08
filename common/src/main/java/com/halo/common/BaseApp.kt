package com.halo.common

import android.app.Application
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates

class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @JvmStatic
        var instance: BaseApp by Delegates.notNull()
            private set
    }

    /**
     * 初始化一些配置
     * 每一个配置的初始化单独使用一个线程来处理
     */
    private fun initConfig() {
        val initRouterObservable = Observable.create<Any> {
            initRouter()
        }.subscribeOn(Schedulers.newThread())

        val initUmengObservable = Observable.create<Any> {
            initUmeng()
        }.subscribeOn(Schedulers.newThread())

        val initRxDownloadObservable = Observable.create<Any> {
            initRxDownload()
        }.subscribeOn(Schedulers.newThread())

        val initLogObservable = Observable.create<Any> {
            initLog()
        }.subscribeOn(Schedulers.newThread())

        val initStrictObservable = Observable.create<Any> {
            initStrict()
        }.subscribeOn(Schedulers.newThread())

        Observable.mergeArray(initRouterObservable, initUmengObservable, initRxDownloadObservable, initLogObservable, initStrictObservable)
                .subscribe()
    }

    private fun initStrict() {
    }

    private fun initLog() {
//        L.header(getHeader())       // 初始化日志框架的Header
//        CrashHandler.getInstance().init(this)
    }

    /**
     * 对于文件下载，可以使用基于 RxJava 和 Kotlin 打造的下载库 RxDownload。它支持多线程下载和断点续传。
     */
    private fun initRxDownload() {

    }

    private fun initUmeng() {

    }

    private fun initRouter() {

    }
}