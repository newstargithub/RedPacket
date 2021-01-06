package com.halo.redpacket.extend

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager

val View.isVisible: Boolean
    get() = visibility == View.VISIBLE
val View.isInVisible: Boolean
    get() = visibility == View.INVISIBLE
val View.isGone: Boolean
    get() = visibility == View.GONE

fun View.hideKeyboard(): Boolean {
    clearFocus()
    return (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard(): Boolean {
    requestFocus()
    return (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(this, 0)
}

fun <T : View> T.gone() {
    this.visibility = View.GONE
}

fun <T : View> T.invisible() {
    this.visibility = View.INVISIBLE
}

fun <T : View> T.visible() {
    this.visibility = View.VISIBLE
}

/**
 * 默认的时间单位是毫秒
 */
inline fun View.postDelayed(delay: Long, crossinline action: () -> Unit): Runnable {
    val runnable = Runnable { action() }
    postDelayed(runnable, delay)
    return runnable
}

/***************************延迟点击相关 Start******************************/

/***
 * 设置延迟时间的View扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @return T
 */
fun <T : View> T.withTrigger(delay: Long = 600): T {
    triggerDelay = delay
    return this
}

/***
 * 点击事件的View扩展
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T: View> T.click(block: (T) -> Unit) = setOnClickListener {
    if (clickEnable()) {
        block(it as T)
    }
}

/***
 * 带延迟过滤的点击事件View扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.clickWithTrigger(time: Long = 600, block: (T) -> Unit){
    triggerDelay = time
    setOnClickListener {
        if (clickEnable()) {
            block(it as T)
        }
    }
}

fun <T: View> T.clickEnable(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        flag = true
    }
    triggerLastTime = currentClickTime
    return flag
}

private var <T : View> T.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else 0
    set(value) {
        setTag(1123460103, value)
    }

private var <T : View> T.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else 600
    set(value) {
        setTag(1123461123, value)
    }

/***
 * 带延迟过滤的点击事件监听 View.OnClickListener
 * 延迟时间根据triggerDelay获取：600毫秒，不能动态设置
 */
interface OnLazyClickListener : View.OnClickListener {

    override fun onClick(v: View?) {
        if (v?.clickEnable() == true) {
            onLazyClick(v)
        }
    }

    fun onLazyClick(v: View)
}
/***************************延迟点击相关 End******************************/

fun <T: View> T.longClick(block: (T) -> Boolean) = setOnLongClickListener { block(this) }

fun View.toBitmap(): Bitmap?{
    clearFocus()
    isPressed = false
    val willNotCache = willNotCacheDrawing()
    setWillNotCacheDrawing(false)
    // Reset the drawing cache background color to fully transparent
    // for the duration of this operation
    val color = drawingCacheBackgroundColor
    drawingCacheBackgroundColor = 0
    if (color != 0) destroyDrawingCache()
    buildDrawingCache()
    val cacheBitmap = drawingCache
    if (cacheBitmap == null) {
        Log.e("Views", "failed to get bitmap from $this", RuntimeException())
        return null
    }
    val bitmap = Bitmap.createBitmap(cacheBitmap)
    // Restore the view
    destroyDrawingCache()
    setWillNotCacheDrawing(willNotCache)
    drawingCacheBackgroundColor = color
    return bitmap
}