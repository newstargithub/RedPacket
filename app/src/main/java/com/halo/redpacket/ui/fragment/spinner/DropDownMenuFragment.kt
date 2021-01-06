package com.halo.redpacket.ui.fragment.spinner

import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.halo.redpacket.R
import com.halo.redpacket.base.BaseFragment
import com.halo.redpacket.extend.then
import com.halo.redpacket.util.ResUtils
import com.halo.redpacket.widget.spinner.DropDownMenu
import kotlinx.android.synthetic.main.fragment_drop_down_menu.*
import kotlinx.android.synthetic.main.layout_drop_down_custom.view.*

class DropDownMenuFragment :BaseFragment() {
    private val mPopupViews: ArrayList<View> = arrayListOf()
    private lateinit var mDropDownMenu: DropDownMenu
    private lateinit var mConstellationAdapter: ConstellationAdapter
    private lateinit var mSexAdapter: ListDropDownAdapter
    private lateinit var mAgeAdapter: ListDropDownAdapter
    private lateinit var mCityAdapter: CityDropDownAdapter
    private lateinit var mSexs: Array<String>
    private lateinit var mAges: Array<String>
    private lateinit var mCitys: Array<String>
    private lateinit var mConstellations: Array<String>

    private val mHeaders = arrayOf("城市", "年龄", "性别", "星座")

    override fun layoutId(): Int {
        return R.layout.fragment_drop_down_menu
    }

    override fun initView() {
        mCitys = ResUtils.getStringArray(R.array.city_entry)
        mAges = ResUtils.getStringArray(R.array.age_entry)
        mSexs = ResUtils.getStringArray(R.array.sex_entry)
        mConstellations = ResUtils.getStringArray(R.array.constellation_entry)

        mDropDownMenu = ddm_content

        val cityView = ListView(context)
        mCityAdapter = CityDropDownAdapter(context!!, mCitys)
        cityView.adapter = mCityAdapter
        cityView.dividerHeight = 0

        //init age menu
        val ageView = ListView(context)
        mAgeAdapter = ListDropDownAdapter(context!!, mAges)
        ageView.adapter = mAgeAdapter
        ageView.dividerHeight = 0

        //init sex menu
        val sexView = ListView(context)
        mSexAdapter = ListDropDownAdapter(context!!, mSexs)
        sexView.adapter = mSexAdapter
        sexView.dividerHeight = 0

        val view = LayoutInflater.from(context).inflate(R.layout.layout_drop_down_custom, null)
        val constellation = view.constellation
        mConstellationAdapter = ConstellationAdapter(context!!, mConstellations)
        constellation.adapter = mConstellationAdapter
        view.btn_ok.setOnClickListener {
            val text = (mConstellationAdapter.getSelectPosition() < 0).then(mHeaders[3], mConstellationAdapter.getSelectItem()!!)
            mDropDownMenu.setTabMenuText(text)
            mDropDownMenu.closeMenu()
        }

        //init mPopupViews
        mPopupViews.add(cityView)
        mPopupViews.add(ageView)
        mPopupViews.add(sexView)
        mPopupViews.add(view)

        //add item click event
        cityView.setOnItemClickListener { parent, view, position, id ->
            mCityAdapter.setSelectPosition(position)
            val text = (mCityAdapter.getSelectPosition() < 0).then(mHeaders[0], mCityAdapter.getSelectItem()!!)
            mDropDownMenu.setTabMenuText(text)
            mDropDownMenu.closeMenu()
        }
        ageView.setOnItemClickListener { parent, view, position, id ->
            mAgeAdapter.setSelectPosition(position)
            val text = (mAgeAdapter.getSelectPosition() < 0).then(mHeaders[1], mAgeAdapter.getSelectItem()!!)
            mDropDownMenu.setTabMenuText(text)
            mDropDownMenu.closeMenu()
        }
        sexView.setOnItemClickListener { parent, view, position, id ->
            mSexAdapter.setSelectPosition(position)
            val text = (mSexAdapter.getSelectPosition() < 0).then(mHeaders[0], mSexAdapter.getSelectItem()!!)
            mDropDownMenu.setTabMenuText(text)
            mDropDownMenu.closeMenu()
        }
        constellation.setOnItemClickListener { parent, view, position, id ->
            mConstellationAdapter.setSelectPosition(position)
        }

        //init context view
        val contentView = TextView(context)
        contentView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        contentView.text = "内容显示区域"
        contentView.gravity = Gravity.CENTER
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)

        //init dropdownview
        mDropDownMenu.setDropDownMenu(mHeaders, mPopupViews, contentView)
    }
}