package com.halo.redpacket.ui.fragment.dialog

import android.view.View
import com.halo.redpacket.extend.toast
import com.halo.redpacket.widget.dialog.bottomsheet.BottomSheet
import java.util.ArrayList

class MaterialDialogFragment: BaseSimpleListFragment() {

    override fun initSimpleData(list: ArrayList<String>): ArrayList<String> {
        list.add("BottomSheet List")
        list.add("BottomSheet Grid")
        return list
    }

    /**
     * 条目点击
     *
     * @param position
     */
    override fun onItemClick(position: Int) {
        when(position) {
            0 ->
                showSimpleBottomSheetList()
            1 ->
                showSimpleBottomSheetGrid()
        }
    }

    private fun showSimpleBottomSheetList() {
        val dialog: BottomSheet = BottomSheet.BottomListSheetBuilder(activity!!)
                .setTitle("标题")
                .addItem("Item 1")
                .addItem("Item 2")
                .addItem("Item 3")
                .setIsCenter(true)
                .setOnSheetItemClickListener(listener = object: BottomSheet.OnSheetItemClickListener {
                    override fun onClick(dialog: BottomSheet, itemView: View, position: Int, tag: String?) {
                        dialog.dismiss()
                        toast("Item " + (position + 1))
                    }
                })
                .build()
        dialog.show()
    }

    private fun showSimpleBottomSheetGrid() {

    }


}