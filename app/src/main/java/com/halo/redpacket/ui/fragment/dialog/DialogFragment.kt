package com.halo.redpacket.ui.fragment.dialog

import com.halo.redpacket.ui.fragment.edittext.CustomEditTextFragment
import com.halo.redpacket.ui.fragment.popwindow.PopupWindowStyleFragment
import com.halo.redpacket.ui.fragment.popwindow.ViewTipFragment
import com.halo.redpacket.ui.fragment.spinner.DropDownMenuFragment

class DialogFragment: PageContainerListFragment() {
    override fun getPagesClasses(): Array<Class<*>>? {
        return arrayOf(MaterialDialogFragment::class.java,
                BottomSheetFragment::class.java,
                PopupWindowStyleFragment::class.java,
                ViewTipFragment::class.java,
                CustomEditTextFragment::class.java,
                DropDownMenuFragment::class.java
        )
    }
}