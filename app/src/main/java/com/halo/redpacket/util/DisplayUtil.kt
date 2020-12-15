package com.halo.redpacket.util

import android.content.Context
import com.halo.redpacket.App
import kotlin.math.roundToInt

class DisplayUtil {

    companion object {
        fun dp2px(context: Context, dp: Float): Int {
            return (context.resources.displayMetrics.density * dp + 0.5f).toInt()
        }

        fun dip2px(dp: Float): Int {
            return (App.instance.resources.displayMetrics.density * dp + 0.5f).toInt()
        }
    }
}