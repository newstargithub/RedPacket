package com.halo.redpacket

import android.app.Application
import androidx.lifecycle.Transformations.map
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        initConfig()
    }

    companion object {
        @JvmStatic
        var instance: App? = null
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
    }

    private fun initRxDownload() {

    }

    private fun initUmeng() {

    }

    private fun initRouter() {

    }
}