package com.halo.redpacket

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.halo.redpacket.extend.errorReturn
import com.halo.redpacket.inject.DaggerUserComponent2
import com.halo.redpacket.inject.IService
import com.halo.redpacket.inject.UserInject
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
import javax.inject.Inject

/**
 * 天气质量
 * zip合并多个网络请求
 */
class PmActivity : AppCompatActivity() {

    @Inject
    lateinit var user: UserInject

    @Inject
    lateinit var service: IService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pm)
        initData()

        /*DaggerUserComponent2.create().inject(this)
        button.setOnClickListener {
//          DaggerUserComponent2.builder().build().inject(this)
            Toast.makeText(this, user.testModule(), Toast.LENGTH_LONG).show()
        }
        button2.setOnClickListener {
            val uri = Uri.parse("wlbapp://android.wlb.com?action=WlbMyCoupon")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
//            Toast.makeText(this, service.foo(), Toast.LENGTH_LONG).show()
        }*/
    }

    private fun initData() {
        val apiService = RetrofitManager.get().pmService()

        val pm25Maybe = apiService.pm25(Constant.CITY_ID, Constant.TOKEN)
                .compose(RxJavaUtils.maybeToMain<List<PM25Model>>())
                .filter { it ->
                    Preconditions.isNotBlank(it)
                }.flatMap { pm25Models ->
                    for (model in pm25Models) {
                        if ("南门" == model.position_name) {
                            return@flatMap Maybe.just(model)
                        }
                    }
                    Maybe.empty<PM25Model>()
                }.errorReturn(PM25Model()) //由于每小时token请求数的限制，可能会导致接口不返回数据。如果不返回数据，则使用默认的PM25Model

        val so2Maybe = apiService.so2(Constant.CITY_ID, Constant.TOKEN)
                .compose(RxJavaUtils.maybeToMain<List<SO2Model>>())
                .filter { it ->
                    Preconditions.isNotBlank(it)
                }.flatMap { so2Models ->
                    for (model in so2Models) {
                        if ("南门" == model.position_name) {
                            return@flatMap Maybe.just(model)
                        }
                    }
                    Maybe.empty<SO2Model>()
                }.errorReturn(SO2Model())

        // 合并多个网络请求
        val subscribe = Maybe.zip(pm25Maybe, so2Maybe, BiFunction<PM25Model, SO2Model, ZipObject> { pm25Model, so2Model ->
            ZipObject(pm25Model.quality,
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
