package com.halo.redpacket.ui.fragment.location

import android.view.animation.Animation
import android.widget.CompoundButton
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.halo.redpacket.R
import com.halo.redpacket.base.BaseFragment
import com.halo.redpacket.extend.clickWithTrigger
import com.halo.redpacket.extend.toast
import com.halo.redpacket.ui.fragment.location.model.City
import com.halo.redpacket.ui.fragment.location.model.HotCity
import com.halo.redpacket.ui.fragment.location.model.LocateState
import kotlinx.android.synthetic.main.fragment_city_picker.*

/**
 * 城市选择
 */
class CityPickerFragment: BaseFragment(), CompoundButton.OnCheckedChangeListener {

    private val mHotCities: List<HotCity>? = null
    private val mAnimStyle: Int = 0
    private val mEnableAnimation: Boolean = false

    override fun layoutId(): Int {
        return R.layout.fragment_city_picker
    }

    override fun initView() {
        initListeners()
    }

    private fun initListeners() {
        cb_hot.setOnCheckedChangeListener(this)
        cb_enable_anim.setOnCheckedChangeListener(this)
        cb_anim.setOnCheckedChangeListener(this)

        btn_style.clickWithTrigger {

        }
        btn_pick.clickWithTrigger {
            CityPicker.from(this)
                    .enableAnimation(mEnableAnimation)
                    .setAnimationStyle(mAnimStyle)
                    .setLocatedCity(null)
                    .setHotCities(mHotCities)
                    .setOnPickListener(object : OnPickListener {
                        var mListener = OnBDLocationListener()
                        override fun onPick(position: Int, city: City) {
                            tv_current.text = "当前城市：${city.name}，${city.code}"
                            LocationService.stop(mListener)
                        }

                        override fun onCancel() {
                            toast("取消选择")
                            LocationService.stop(mListener)
                        }

                        override fun onLocate(listener: OnLocationListener) {
                            //开始定位
                            mListener.setOnLocationListener(listener)
                            LocationService.start(mListener)
                        }
                    })
                    .show()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when(buttonView.id) {
            R.id.cb_hot -> {

            }
            R.id.cb_enable_anim -> {

            }
            R.id.cb_anim -> {

            }
        }
    }

    /**
     * 百度定位
     */
    class OnBDLocationListener: BDAbstractLocationListener() {
        private var onLocationListener: OnLocationListener? = null

        fun setOnLocationListener(listener: OnLocationListener?): OnBDLocationListener {
            onLocationListener = listener
            return this
        }

        override fun onReceiveLocation(bdLocation: BDLocation) {
            onLocationListener?.let {
                it.onLocationChanged(LocationService.onReceiveLocation(bdLocation), LocateState.SUCCESS)
                LocationService.get().unregisterListener(this)
            }
        }
    }
}