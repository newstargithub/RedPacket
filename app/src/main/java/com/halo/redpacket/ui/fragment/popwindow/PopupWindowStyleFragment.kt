package com.halo.redpacket.ui.fragment.popwindow

import android.view.View
import com.halo.redpacket.R
import com.halo.redpacket.base.BaseFragment
import com.halo.redpacket.extend.dp2px
import com.halo.redpacket.extend.toast
import com.halo.redpacket.ui.fragment.DemoProvider
import com.halo.redpacket.widget.popup.AdapterItem
import com.halo.redpacket.widget.popup.XUISimpleAdapter
import com.halo.redpacket.widget.popup.XUISimplePopup
import kotlinx.android.synthetic.main.fragment_popupwindow_style.*

class PopupWindowStyleFragment: BaseFragment(), View.OnClickListener {

    private var mMenuPopup: XUISimplePopup? = null
    private var mListPopup: XUISimplePopup? = null

    override fun layoutId(): Int {
        return R.layout.fragment_popupwindow_style
    }

    override fun initView() {
        initListPopup()
        initExpandableListPopup()
        initMenuPopup()
        btn_commonlist_popup.setOnClickListener(this)
        btn_expandable_popup.setOnClickListener(this)
        btn_menu_popup.setOnClickListener(this)
    }

    fun initListPopup() {
        context?.let {
            this.mListPopup = XUISimplePopup(it, DemoProvider.dpiItems)
                    .create(it.dp2px(170f), object : XUISimplePopup.OnPopupItemClickListener {
                        override fun onItemClick(adapter: XUISimpleAdapter, item: AdapterItem, position: Int) {
                            toast(item.getTitle().toString())
                        }
                    })
                    .setHasDivider(true)
        }
    }

    private fun initMenuPopup() {
        mMenuPopup = context?.let {
            XUISimplePopup(it, DemoProvider.menuItems)
                    .create(it.dp2px(170f), object : XUISimplePopup.OnPopupItemClickListener {
                        override fun onItemClick(adapter: XUISimpleAdapter, item: AdapterItem, position: Int) {
                            toast(item.getTitle().toString())
                        }
                    })
                    .setHasDivider(true)
        }
    }

    private fun initExpandableListPopup() {

    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_commonlist_popup -> {
                mListPopup?.showDown(v)
            }
            R.id.btn_menu_popup -> {
                mMenuPopup?.showDown(v)
            }
        }
    }
}