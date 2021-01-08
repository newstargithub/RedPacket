package com.halo.redpacket.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 屏幕工具
 *
 * @author xuexiang
 * @since 2018/12/30 下午6:42
 */
class ScreenUtils {

    private fun ScreenUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {
        private fun getInternalDimensionSize(context: Context, key: String): Int {
            var result = 0
            try {
                val resourceId = context.resources.getIdentifier(key, "dimen", "android")
                if (resourceId > 0) {
                    result = Math.round(context.resources.getDimensionPixelSize(resourceId) *
                            Resources.getSystem().displayMetrics.density /
                            context.resources.displayMetrics.density)
                }
            } catch (ignored: NotFoundException) {
                return 0
            }
            return result
        }

        fun getStatusBarHeight(context: Context): Int {
            return getInternalDimensionSize(context, "status_bar_height")
        }

        fun getNavigationBarHeight(context: Context): Int {
            val mInPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            val result = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (hasNavBar(context as Activity)) {
                    val key = if (mInPortrait) {
                        "navigation_bar_height"
                    } else {
                        "navigation_bar_height_landscape"
                    }
                    return getInternalDimensionSize(context, key)
                }
            }
            return result
        }

        private fun hasNavBar(activity: Activity): Boolean {
            //判断小米手机是否开启了全面屏,开启了，直接返回false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (Settings.Global.getInt(activity.contentResolver, "force_fsg_nav_bar", 0) != 0) {
                    return false
                }
            }
            //其他手机根据屏幕真实高度与显示高度是否相同来判断
            val windowManager = activity.windowManager
            val d = windowManager.defaultDisplay
            val realDisplayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                d.getRealMetrics(realDisplayMetrics)
            }
            val realHeight = realDisplayMetrics.heightPixels
            val realWidth = realDisplayMetrics.widthPixels
            val displayMetrics = DisplayMetrics()
            d.getMetrics(displayMetrics)
            val displayHeight = displayMetrics.heightPixels
            val displayWidth = displayMetrics.widthPixels
            return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
        }

        /**
         * 动态隐藏软键盘
         *
         * @param view 视图
         */
        fun hideSoftInput(view: View) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager ?: return
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}