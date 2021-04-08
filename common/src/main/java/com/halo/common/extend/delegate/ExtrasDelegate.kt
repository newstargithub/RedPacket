package com.halo.redpacket.extend.delegate

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlin.reflect.KProperty

/**
 * 委托类
 * 使用委托属性的方式来封装 Extras，支持在 AppCompatActivity 和 Fragment 中使用。
 *
 * 上一个页面所传递过来的任何对象类型，都可以使用如下的方式获取。
 *  private val user: User? by extraDelegate("user")
    private val s:String? by extraDelegate("string")
 */
class ExtrasDelegate<out T> (private val extraName: String, private val defaultValue: T){
    private var extra: T? = null

    operator fun getValue(thisRef: AppCompatActivity, kProperty: KProperty<*>): T {
        extra = getExtra(extra, extraName, thisRef)
        return extra ?: defaultValue
    }

    operator fun getValue(thisRef: Fragment, kProperty: KProperty<*>): T {
        extra = getExtra(extra, extraName, thisRef)
        return extra ?: defaultValue
    }
}

fun extraDelegate(extraName: String) = extraDelegate(extraName, null)

fun<T> extraDelegate(extraName: String, defaultValue: T) = ExtrasDelegate(extraName, defaultValue)

@Suppress("UNCHECKED_CAST")
private fun<T> getExtra(oldExtra: T?, extraName: String, thisRef: AppCompatActivity): T? {
    return oldExtra ?: thisRef.intent?.extras?.get(extraName) as T?
}

@Suppress("UNCHECKED_CAST")
private fun<T> getExtra(oldExtra: T?, extraName: String, thisRef: Fragment): T? {
    return oldExtra ?: thisRef.arguments?.get(extraName) as T?
}