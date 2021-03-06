package com.halo.redpacket.ui.fragment.location

import android.content.Context
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.location.LocationClientOption.LocationMode
import com.halo.redpacket.ui.fragment.location.model.LocatedCity
import com.safframework.ext.TAG

class LocationService {

    private var mDIYOption: LocationClientOption? = null
    private var mOption: LocationClientOption? = null
    private var mClient: LocationClient? = null
    var mLocatedCity: LocatedCity? = null

    companion object {
        private var sInstance: LocationService? = null

        /**
         * 停止定位
         */
        fun stop(listener: BDAbstractLocationListener) {
            get().unregisterListener(listener).stop()
        }

        /**
         * 开始定位
         *
         * @param listener
         * 需要权限
         */
        fun start(listener: BDAbstractLocationListener) {
            get().registerListener(listener).start()
        }

        /**
         * 获取单例
         *
         * @return
         */
        fun get(): LocationService {
            if (sInstance == null) {
                synchronized(LocationService::class) {
                    if (sInstance == null) {
                        sInstance = LocationService()
                    }
                }
            }
            return sInstance!!
        }

        /**
         * 当收到定位信息
         *
         * @param bdLocation
         * @return
         */
        fun onReceiveLocation(bdLocation: BDLocation): LocatedCity {
            if (get().mLocatedCity == null || bdLocation.city != null) {
                get().mLocatedCity = LocatedCity(bdLocation.city, bdLocation.province, bdLocation.cityCode)
            }
            return get().mLocatedCity!!
        }
    }

    /**
     * 停止定位
     */
    private fun stop(): LocationService {
        mClient?.apply {
            if (!isStarted) {
                stop()
            }
        }
        return this
    }

    /**
     * 注销定位监听
     *
     * @param listener
     */
    fun unregisterListener(listener: BDAbstractLocationListener): LocationService {
        mClient?.unRegisterLocationListener(listener)
        return this
    }

    /**
     * 开始定位
     */
    private fun start() {
        mClient?.apply {
            if (!isStarted) {
                start()
            }
        }
    }

    /***
     * 初始化
     * @param context
     */
    fun init(context: Context) {
        if (mClient == null) {
            mClient = LocationClient(context.applicationContext)
            mClient?.setLocOption(getDefaultLocationClientOption())
        }
    }

    /***
     * 注册定位监听
     * @param listener
     * @return
     */
    fun registerListener(listener: BDAbstractLocationListener): LocationService {
        mClient?.registerLocationListener(listener)
        return this
    }

    fun isStart(): Boolean {
        return mClient?.isStarted ?: false
    }

    fun requestHotSpotState(): Boolean {
        return mClient?.requestHotSpotState() ?: false
    }

    /***
     *
     * @return DefaultLocationClientOption  默认O设置
     */
    private fun getDefaultLocationClientOption(): LocationClientOption {
        if (mOption == null) {
            mOption = LocationClientOption().apply {
                setLocationMode(LocationMode.Hight_Accuracy) //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
                setCoorType("bd09ll") //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
                setScanSpan(0) //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
                setIsNeedAddress(true) //可选，设置是否需要地址信息，默认不需要
                setIsNeedLocationDescribe(true) //可选，设置是否需要地址描述
                setNeedDeviceDirect(false) //可选，设置是否需要设备方向结果
                setLocationNotify(false) //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
                setIgnoreKillProcess(true) //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
                setIsNeedLocationDescribe(true) //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
                setIsNeedLocationPoiList(true) //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
                SetIgnoreCacheException(false) //可选，默认false，设置是否收集CRASH信息，默认收集
                setOpenGps(true) //可选，默认false，设置是否开启Gps定位
                setIsNeedAltitude(false) //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
            }
        }
        return mOption!!
    }

    /***
     * 设置定位参数
     * @param option
     * @return
     */
    fun setLocationOption(option: LocationClientOption?): Boolean {
        if (option != null) {
            if (mClient!!.isStarted) {
                mClient!!.stop()
            }
            mDIYOption = option
            mClient!!.locOption = option
            return true
        }
        return false
    }

    /**
     * 打印地址信息
     *
     * @param location
     */
    fun printLocationInfo(location: BDLocation?) {
        if (null != location && location.locType != BDLocation.TypeServerError) {
            val sb = StringBuilder(256)
            sb.append("time : ")
            /**
             * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
             * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
             */
            sb.append(location.time)
            sb.append("\nlocType : ") // 定位类型
            sb.append(location.locType)
            sb.append("\nlocType description : ") // *****对应的定位类型说明*****
            sb.append(location.locTypeDescription)
            sb.append("\nlatitude : ") // 纬度
            sb.append(location.latitude)
            sb.append("\nlontitude : ") // 经度
            sb.append(location.longitude)
            sb.append("\nradius : ") // 半径
            sb.append(location.radius)
            sb.append("\nCountryCode : ") // 国家码
            sb.append(location.countryCode)
            sb.append("\nCountry : ") // 国家名称
            sb.append(location.country)
            sb.append("\ncitycode : ") // 城市编码
            sb.append(location.cityCode)
            sb.append("\ncity : ") // 城市
            sb.append(location.city)
            sb.append("\nDistrict : ") // 区
            sb.append(location.district)
            sb.append("\nStreet : ") // 街道
            sb.append(location.street)
            sb.append("\naddr : ") // 地址信息
            sb.append(location.addrStr)
            sb.append("\nUserIndoorState: ") // *****返回用户室内外判断结果*****
            sb.append(location.userIndoorState)
            sb.append("\nDirection(not all devices have value): ")
            sb.append(location.direction) // 方向
            sb.append("\nlocationdescribe: ")
            sb.append(location.locationDescribe) // 位置语义化信息
            sb.append("\nPoi: ") // POI信息
            if (location.poiList != null && !location.poiList.isEmpty()) {
                for (i in location.poiList.indices) {
                    val poi = location.poiList[i]
                    sb.append(poi.name + ";")
                }
            }
            if (location.locType == BDLocation.TypeGpsLocation) { // GPS定位结果
                sb.append("\nspeed : ")
                sb.append(location.speed) // 速度 单位：km/h
                sb.append("\nsatellite : ")
                sb.append(location.satelliteNumber) // 卫星数目
                sb.append("\nheight : ")
                sb.append(location.altitude) // 海拔高度 单位：米
                sb.append("\ngps status : ")
                sb.append(location.gpsAccuracyStatus) // *****gps质量判断*****
                sb.append("\ndescribe : ")
                sb.append("gps定位成功")
            } else if (location.locType == BDLocation.TypeNetWorkLocation) { // 网络定位结果
                // 运营商信息
                if (location.hasAltitude()) { // *****如果有海拔高度*****
                    sb.append("\nheight : ")
                    sb.append(location.altitude) // 单位：米
                }
                sb.append("\noperationers : ") // 运营商信息
                sb.append(location.operators)
                sb.append("\ndescribe : ")
                sb.append("网络定位成功")
            } else if (location.locType == BDLocation.TypeOffLineLocation) { // 离线定位结果
                sb.append("\ndescribe : ")
                sb.append("离线定位成功，离线定位结果也是有效的")
            } else if (location.locType == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ")
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因")
            } else if (location.locType == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ")
                sb.append("网络不同导致定位失败，请检查网络是否通畅")
            } else if (location.locType == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ")
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机")
            }
            Log.i(TAG(), sb.toString())
        }
    }

}