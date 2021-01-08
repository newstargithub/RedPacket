package com.halo.redpacket.ui.fragment.location

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.halo.redpacket.ui.fragment.location.data.DBCityCenter
import com.halo.redpacket.ui.fragment.location.data.ICityCenter
import com.halo.redpacket.ui.fragment.location.model.City
import com.halo.redpacket.ui.fragment.location.model.HotCity
import com.halo.redpacket.ui.fragment.location.model.LocatedCity
import com.safframework.ext.TAG
import java.lang.ref.WeakReference

/**
 * 城市选择器
 *
 * @author xuexiang
 * @since 2018/12/29 上午11:54
 */
class CityPicker(manager: FragmentManager) {


    private var mOnPickListener: OnPickListener? = null
    private var mHotCities: List<HotCity>? = null
    private var mLocation: City? = null
    private var mAnimStyle: Int = 0
    private var mEnableAnim: Boolean = false

    var mFragmentManager: WeakReference<FragmentManager> = WeakReference(manager)

    constructor(activity: FragmentActivity) : this(activity.supportFragmentManager)

    constructor(fragment: Fragment) : this(fragment.childFragmentManager)

    /**
     * 启用动画效果，默认为false
     *
     * @param enable
     * @return
     */
    fun enableAnimation(enableAnimation: Boolean): CityPicker {
        mEnableAnim = enableAnimation
        return this
    }

    /**
     * 设置动画效果
     *
     * @param animStyle
     * @return
     */
    fun setAnimationStyle(animStyle: Int): CityPicker {
        mAnimStyle = animStyle
        return this
    }

    /**
     * 设置当前已经定位的城市
     *
     * @param location
     * @return
     */
    fun setLocatedCity(location: LocatedCity?): CityPicker {
        mLocation = location
        return this
    }

    fun setHotCities(hotCities: List<HotCity>?): CityPicker {
        mHotCities = hotCities
        return this
    }

    /**
     * 设置选择结果的监听器
     *
     * @param listener
     * @return
     */
    fun setOnPickListener(listener: OnPickListener?): CityPicker {
        mOnPickListener = listener
        return this
    }

    fun show() {
        val fm = mFragmentManager.get()
        fm?.let {
            val ft = it.beginTransaction()
            val prev = it.findFragmentByTag(CityPicker.TAG())
            if (prev != null) {
                ft.remove(prev).commit()
            }
            ft.addToBackStack(null)
            val fragment = CityPickerDialogFragment.newInstance(mEnableAnim)
            fragment.setLocatedCity(mLocation)
            fragment.setHotCities(mHotCities)
            fragment.setAnimationStyle(mAnimStyle)
            fragment.setOnPickListener(mOnPickListener)
            fragment.show(ft, CityPicker.TAG())
        }
    }

    companion object {

        private var sICityCenter: ICityCenter? = null

        fun from(fragment: Fragment): CityPicker {
            return CityPicker(fragment)
        }

        fun from(activity: FragmentActivity): CityPicker {
            return CityPicker(activity)
        }

        /**
         * 获取城市信息中心的实现类
         *
         * @param context
         * @return
         */
        fun getICityCenter(context: Context): ICityCenter {
            if (sICityCenter == null) {
                sICityCenter = DBCityCenter(context)
            }
            return sICityCenter!!
        }
    }
}