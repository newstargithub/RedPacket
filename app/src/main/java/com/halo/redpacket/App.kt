package com.halo.redpacket

import android.app.Application
import androidx.lifecycle.Transformations.map
import com.halo.redpacket.ui.fragment.location.LocationService
import com.halo.redpacket.util.CrashHandler
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        initConfig()
    }

    companion object {
        @JvmStatic
        var instance: App by Delegates.notNull()
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
        LocationService.get().init(this)
    }

    private fun initLog() {
//        L.header(getHeader())       // 初始化日志框架的Header
        CrashHandler.getInstance().init(this)
    }

    /**
     * 对于文件下载，可以使用基于 RxJava 和 Kotlin 打造的下载库 RxDownload。它支持多线程下载和断点续传。
     */
    private fun initRxDownload() {
        /*DownloadConfig.Builder.create(this)
                .enableAutoStart(true)              //自动开始下载
                .setDefaultPath("custom download path")     //设置默认的下载地址
                .useHeadMethod(true)    //使用http HEAD方法进行检查
                .setMaxRange(10)       // 每个任务并发的线程数量
                .setRangeDownloadSize(4*1000*1000) //每个线程下载的大小，单位字节
                .setMaxMission(3)      // 同时下载的任务数量
                .enableDb(true)                             //启用数据库
                .setDbActor(CustomSqliteActor(this))        //自定义数据库
                .enableService(true)                        //启用Service
                .enableNotification(true)                   //启用Notification
                .setNotificationFactory(NotificationFactoryImpl()) 	    //自定义通知
                .setOkHttpClientFacotry(OkHttpClientFactoryImpl()) 	    //自定义OKHTTP
                .addExtension(ApkInstallExtension::class.java)          //添加扩展*/
    }

    private fun initUmeng() {

    }

    private fun initRouter() {

    }
}