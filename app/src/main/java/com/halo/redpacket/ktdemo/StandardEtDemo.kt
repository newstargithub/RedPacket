package com.halo.redpacket.ktdemo

import android.content.Context
import android.net.ConnectivityManager
import android.view.View

/**
 * 常用标准库的扩展函数
 * it 和 this 指向调用对象本身
 */
class StandardDemo {
}

/**
 * with 是将某个对象作为函数的参数，在函数块内可以通过 this 指代该对象。在函数块内可以直接调用对象的方法或者属性。
 */

/**
 * apply 函数是指在函数块内可以通过 this 指代该对象，返回值为该对象自己。在链式调用中，我们可以考虑使用它，从而不用破坏链式。
 */
fun applyTest() {
    val result = "Hello ".apply {
        println("$this World")
        this + "World" // apply 会返回该对象自己，所以 result 的值依然是“Hello”
    }
    println(result)
}
/**
 * run 函数类似于 apply 函数，但是 run 函数返回的是最后一行的值。
 */
fun runTest() {
    val result = "Hello ".run {
        println("$this World")
        this + "World" // run 返回的是最后一行的值
    }
    println(result)
}
/**
 * let 函数把当前对象作为闭包的 it 参数，返回值是函数里面最后一行，或者指定 return。它看起来有点类似于 run 函数。
 * let 函数跟 run 函数的区别是：let 函数在函数内可以通过 it 指代该对象。
 *
 * 通常情况下，let 函数跟?结合使用：obj?.let {....}
 * 可以在 obj 不为 null 的情况下执行 let 函数块的代码，从而避免了空指针异常的出现。
 */
fun letTest() {
    val result = "Hello ".let {
        println("$it World")
        it + "World" // let 返回的是最后一行的值
    }
    println(result)
}
/**
 * 类似于 apply 的功能。跟 apply 不同的是，also 在函数块内可以通过 it 指代该对象，返回值为该对象自己。
 */
fun alsoTest() {
    val result = "Hello ".also {
        println("$it World")
        it + "World"
    }
    println(result)
}

/**
 * 对 Context 添加一个属性 isNetworkAvailable：
 */
val Context.isNetworkAvailable : Boolean
    get() {
        val cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

//对 View 添加一个扩展函数：
fun <T : View> T.click1(block: (T) -> Unit) = setOnClickListener { block(it as T) }

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
fun <T : View> T.click(block: (T) -> Unit) = setOnClickListener {
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
fun <T : View> T.clickWithTrigger(delay: Long, block: (T) -> Unit) = setOnClickListener {
    triggerDelay = delay
    setOnClickListener {
        if (clickEnable()) {
            block(it as T)
        }
    }
}

/**
 * 上次触发时间
 */
private var <T : View> T.triggerLastTime : Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else 0
    set(value) = setTag(1123460103, value)

/**
 * 触发延迟时间
 */
private var <T : View> T.triggerDelay : Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else 0
    set(value) = setTag(1123461123, value)

private fun <T : View> T.clickEnable(): Boolean {
    var flag = false
    val currentTimeMillis = System.currentTimeMillis()
    if (currentTimeMillis > triggerLastTime + triggerDelay) {
        flag = true
        triggerLastTime = currentTimeMillis
    }
    return flag
}