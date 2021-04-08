package com.halo.redpacket.extend

import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.halo.redpacket.App
import com.halo.redpacket.R
import com.halo.redpacket.util.AppUtils.Companion.getApplicationContext
import com.halo.redpacket.util.DisplayUtil

fun Toast.setGravityCenter(): Toast {
    setGravity(Gravity.CENTER, 0, 0)
    return this
}

/**
 * 设置Toast字体及背景颜色
 * @param messageColor
 * @param backgroundColor
 * @return
 */
fun Toast.setToastColor(@ColorInt messageColor: Int, @ColorInt backgroundColor: Int) {
    val view = view
    if (view != null) {
        val message = view.findViewById(android.R.id.message) as TextView
        message.setBackgroundColor(backgroundColor)
        message.setTextColor(messageColor)
    }
}

/**
 * 设置Toast字体及背景
 * @param messageColor
 * @param background
 * @return
 */
fun Toast.setBackground(@ColorInt messageColor: Int = Color.WHITE, @DrawableRes background: Int = R.drawable.shape_toast_bg): Toast {
    val view = view
    if (view != null) {
        val message = view.findViewById(android.R.id.message) as TextView
        view.setBackgroundResource(background)
        message.setTextColor(messageColor)
    }
    return this
}

//@SuppressLint("ShowToast")
fun toast(text: CharSequence): Toast = Toast.makeText(App.instance, text, Toast.LENGTH_LONG)
        .setGravityCenter()
        .setBackground()
//需要的地方调用withErrorIcon，默认不要添加
//        .withErrorIcon()


//@SuppressLint("ShowToast")
fun toast(@StringRes res: Int): Toast = Toast.makeText(App.instance, App.instance.resources.getString(res), Toast.LENGTH_LONG)
        .setGravityCenter()
        .setBackground()
//需要的地方调用withErrorIcon，默认不要添加
//        .withErrorIcon()

fun Toast.withErrorIcon(@DrawableRes iconRes: Int = R.drawable.ic_toast_error): Toast {
    val view = view
    if (view != null) {
        val layout = this.view as LinearLayout
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val icon = ImageView(getApplicationContext())
        icon.setImageResource(iconRes)
        icon.setPadding(0, 0, DisplayUtil.dip2px(8f), 0)
        icon.layoutParams = layoutParams
        layout.orientation = LinearLayout.HORIZONTAL
        layout.gravity = Gravity.CENTER_VERTICAL
        layout.addView(icon, 0)
    }
    return this
}

fun Toast.withSuccIcon(@DrawableRes iconRes: Int = R.drawable.ic_right_circle): Toast {
    val view = view
    if (view != null) {
        val layout = this.view as LinearLayout
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val icon = ImageView(getApplicationContext())
        icon.setImageResource(iconRes)
        icon.setPadding(0, 0, DisplayUtil.dip2px(8f), 0)
        icon.layoutParams = layoutParams
        layout.orientation = LinearLayout.HORIZONTAL
        layout.gravity = Gravity.CENTER_VERTICAL
        layout.addView(icon, 0)
    }
    return this
}