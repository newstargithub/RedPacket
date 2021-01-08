package com.halo.redpacket.ui.fragment.location

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import com.halo.redpacket.R
import com.halo.redpacket.ui.fragment.location.data.ICityCenter
import com.halo.redpacket.ui.fragment.location.model.City
import com.halo.redpacket.ui.fragment.location.model.HotCity
import com.halo.redpacket.ui.fragment.location.model.LocateState
import com.halo.redpacket.ui.fragment.location.model.LocatedCity
import com.halo.redpacket.util.ScreenUtils

class CityPickerDialogFragment: DialogFragment() {

    private var height: Int = 0
    private var width: Int = 0
    private var mOnPickListener: OnPickListener? = null
    private lateinit var mResults: MutableList<City>
    private lateinit var mAllCities: MutableList<City>
    private lateinit var mCityCenter: ICityCenter
    private var locateState: Int = 0
    private var enableAnim: Boolean = false
    private lateinit var mContentView: View
    private var mAnimStyle: Int = R.style.CityPickerAnimation
    private var mHotCities: MutableList<HotCity>? = null
    private var mLocatedCity: City? = null

    fun setLocatedCity(location: City) {
        mLocatedCity = location
    }

    fun setHotCities(hotCities: ArrayList<HotCity>?) {
        if (!hotCities.isNullOrEmpty()) {
            mHotCities = hotCities
        }
    }

    fun setAnimationStyle(@StyleRes animStyle: Int) {
        mAnimStyle = if(animStyle == 0) mAnimStyle else animStyle
    }

    fun setOnPickListener(listener: OnPickListener?) {
        mOnPickListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentView = inflater.inflate(R.layout.cp_dialog_city_picker, container, false)
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViews()
    }

    private fun initViews() {

    }

    private fun initData() {
        arguments?.apply {
            enableAnim = getBoolean(KEY_ENABLE_ANIM)
        }
        //初始化热门城市
        if (mHotCities.isNullOrEmpty()) {
            mHotCities = arrayListOf()
            mHotCities?.apply {
                add(HotCity("北京", "北京", "101010100"))
                add(HotCity("上海", "上海", "101020100"))
                add(HotCity("广州", "广东", "101280101"))
                add(HotCity("深圳", "广东", "101280601"))
                add(HotCity("天津", "天津", "101030100"))
                add(HotCity("杭州", "浙江", "101210101"))
                add(HotCity("南京", "江苏", "101190101"))
                add(HotCity("成都", "四川", "101270101"))
                add(HotCity("武汉", "湖北", "101200101"))
            }
        }
        //初始化定位城市，默认为空时会自动回调定位
        if (mLocatedCity == null) {
            mLocatedCity = LocatedCity(getString(R.string.cp_locating), "未知", "0")
            locateState = LocateState.LOCATING
        } else {
            locateState = LocateState.SUCCESS
        }
        mCityCenter = CityPicker.getICityCenter(context!!)
        mAllCities = mCityCenter.getAllCities()
        mAllCities.add(0, mLocatedCity!!)
        mAllCities.add(1, HotCity("热门城市", "未知", "0"))
        mResults = mAllCities
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                mOnPickListener?.onCancel()
            }
            return@setOnKeyListener false
        }

        measure()
        val window = dialog.window
        window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            it.setLayout(width, height - ScreenUtils.getStatusBarHeight(activity!!))
            if (enableAnim) {
                it.setWindowAnimations(mAnimStyle)
            }
        }
    }

    private fun measure() {
        val dm: DisplayMetrics
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            dm = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(dm)
        } else {
            dm = getResources().getDisplayMetrics()
        }
        height = dm.heightPixels
        width = dm.widthPixels
    }

    companion object {
        private val KEY_ENABLE_ANIM = "key_enable_anim"

        fun newInstance(enableAnim: Boolean): CityPickerDialogFragment {
            val fragment = CityPickerDialogFragment()
            fragment.arguments = Bundle().apply {
                putBoolean(KEY_ENABLE_ANIM, enableAnim)
            }
            return fragment
        }
    }
}