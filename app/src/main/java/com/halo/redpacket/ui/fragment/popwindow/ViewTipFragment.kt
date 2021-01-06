package com.halo.redpacket.ui.fragment.popwindow

import android.graphics.Color
import com.halo.redpacket.R
import com.halo.redpacket.base.BaseFragment
import com.halo.redpacket.widget.viewtip.TooltipView
import com.halo.redpacket.widget.viewtip.ViewTooltip
import kotlinx.android.synthetic.main.fragment_view_tip.*

class ViewTipFragment: BaseFragment() {

    override fun layoutId(): Int {
        return R.layout.fragment_view_tip
    }

    override fun initView() {
        btn_left.setOnClickListener {
            ViewTooltip.on(editText)
                    .text("Some tooltip with long text")
                    .color(Color.BLACK)
                    .align(TooltipView.ALIGN.CENTER)
                    .autoHide(false)
                    .clickToHide(true)
                    .position(TooltipView.Position.LEFT)
                    .show()
        }
        btn_top.setOnClickListener {
            ViewTooltip.on(editText)
                    .text("Some tooltip with long text")
                    .color(Color.BLACK)
                    .align(TooltipView.ALIGN.CENTER)
                    .autoHide(true)
                    .position(TooltipView.Position.TOP)
                    .show()
        }
        btn_right.setOnClickListener {
            ViewTooltip.on(editText)
                    .text("Some tooltip with long text")
                    .color(Color.BLACK)
                    .align(TooltipView.ALIGN.CENTER)
                    .autoHide(true)
                    .position(TooltipView.Position.RIGHT)
                    .show()
        }
        btn_bottom.setOnClickListener {
            ViewTooltip.on(editText)
                    .text("Some tooltip with long text")
                    .color(Color.BLACK)
                    .align(TooltipView.ALIGN.CENTER)
                    .autoHide(true)
                    .position(TooltipView.Position.BOTTOM)
                    .show()
        }
        bottomLeft.setOnClickListener {
            ViewTooltip
                    .on(bottomLeft)
                    .position(TooltipView.Position.TOP)
                    .text("bottomLeft bottomLeft bottomLeft")
                    .show()
        }
        bottomRight.setOnClickListener {
            ViewTooltip
                    .on(bottomRight)
                    .position(TooltipView.Position.TOP)
                    .text("bottomRight bottomRight bottomRight")
                    .show()
        }
    }
}