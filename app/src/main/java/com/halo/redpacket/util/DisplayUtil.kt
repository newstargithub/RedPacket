package com.halo.redpacket.util

import android.content.Context
import kotlin.math.roundToInt

class DisplayUtil {

    companion object {
        fun dp2px(context: Context, dp: Float): Int {
            return (context.resources.displayMetrics.density * dp + 0.5f).toInt()
        }
    }
}