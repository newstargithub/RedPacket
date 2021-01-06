package com.halo.redpacket.ui.fragment.dialog

import android.view.View
import com.halo.redpacket.R
import com.halo.redpacket.extend.toast
import com.halo.redpacket.widget.dialog.bottomsheet.BottomSheet
import com.halo.redpacket.widget.dialog.bottomsheet.BottomSheetItemView
import java.util.*

class BottomSheetFragment: BaseSimpleListFragment() {

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
        val TAG_SHARE_WECHAT_FRIEND = 0
        val TAG_SHARE_WECHAT_MOMENT = 1
        val TAG_SHARE_WEIBO = 2
        val TAG_SHARE_CHAT = 3
        val TAG_SHARE_LOCAL = 4
        val dialog: BottomSheet = BottomSheet.BottomGridSheetBuilder(activity!!)
                .addItem(R.drawable.icon_more_operation_share_friend, "分享到微信", TAG_SHARE_WECHAT_FRIEND, BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.icon_more_operation_share_moment, "分享到朋友圈", TAG_SHARE_WECHAT_MOMENT, BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.icon_more_operation_share_weibo, "分享到微博", TAG_SHARE_WEIBO, BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.icon_more_operation_share_chat, "分享到私信", TAG_SHARE_CHAT, BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.icon_more_operation_save, "保存到本地", TAG_SHARE_LOCAL, BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                .setOnSheetItemClickListener(listener = object: BottomSheet.BottomGridSheetBuilder.OnSheetItemClickListener {
                    override fun onClick(dialog: BottomSheet, itemView: BottomSheetItemView) {
                        dialog.dismiss()
                        val tag = itemView.tag as Int
                        toast("tag:$tag, content:$itemView")
                    }
                })
                .build()
        dialog.show()
    }

}