package com.halo.redpacket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.halo.redpacket.model.Preconditions
import com.halo.redpacket.model.RetrofitManager
import com.halo.redpacket.model.bean.PM25Model
import com.halo.redpacket.model.bean.SO2Model
import com.halo.redpacket.model.bean.ZipObject
import com.halo.redpacket.util.Constant
import com.halo.redpacket.util.RxJavaUtils
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_pm.*

class PmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pm)
        initData()
    }

    private fun initData() {
        val pmService = RetrofitManager.get().pmService()

        val pm25Maybe = pmService.pm25(Constant.CITY_ID, Constant.TOKEN)
                .compose(RxJavaUtils.maybeToMain())
                .filter { it ->
                    Preconditions.isNotBlank(it)
                }.flatMap { pm25Models ->
                    for (model in pm25Models) {
                        if ("南门" == model.position_name) {
                            return@flatMap Maybe.just(model)
                        }
                    }
                    return@flatMap Maybe.empty<PM25Model>()
                }.onErrorReturn {
                    //由于每小时token请求数的限制，可能会导致接口不返回数据。如果不返回数据，则使用默认的PM25Model
                    PM25Model()
                }

        val so2Maybe = pmService.so2(Constant.CITY_ID, Constant.TOKEN)
                .compose(RxJavaUtils.maybeToMain())
                .filter { it ->
                    Preconditions.isNotBlank(it)
                }.flatMap { so2Models ->
                    for (model in so2Models) {
                        if ("南门" == model.position_name) {
                            return@flatMap Maybe.just(model)
                        }
                    }
                    return@flatMap Maybe.empty<SO2Model>()
                }.onErrorReturnItem(SO2Model())

        // 合并多个网络请求
        Maybe.zip(pm25Maybe, so2Maybe, BiFunction<PM25Model, SO2Model, ZipObject>{ pm25Model, so2Model->
            return@BiFunction ZipObject(pm25Model.quality,
                    pm25Model.pm2_5,
                    pm25Model.pm2_5_24h,
                    so2Model.so2,
                    so2Model.so2_24h)
        }).subscribe({
            quality.setText("空气质量指数：" + it.pm2_5_quality)        // 如果为空，表示使用了默认值
            pm2_5.setText("PM2.5 1小时内平均：" + it.pm2_5)             // 如果为0，表示使用了默认值
            pm2_5_24h.setText("PM2.5 24小时滑动平均：" + it.pm2_5_24h)  // 如果为0，表示使用了默认值

            so2.setText("二氧化硫1小时平均：" + it.so2)  // 如果为0，表示使用了默认值
            so2_24h.setText("二氧化硫24小时滑动平均：" + it.so2_24h) // 如果为0，表示使用了默认值
        }, {
            println(it.message)
        })
    }
}
