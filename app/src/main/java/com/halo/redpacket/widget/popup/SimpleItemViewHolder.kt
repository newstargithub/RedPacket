package com.halo.redpacket.widget.popup

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class SimpleItemViewHolder {
    /**
     * 父布局
     */
    lateinit var mLLContentView: LinearLayout

    /**
     * 标题
     */
    lateinit var mTvTitle: TextView

    /**
     * 图标
     */
    lateinit var mIvIcon: ImageView
}