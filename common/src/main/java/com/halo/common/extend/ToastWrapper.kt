package com.halo.redpacket.extend

import android.widget.Toast
import com.halo.redpacket.R

/**
 * @author Zhouxin
 * @date :2021/3/31
 * @description:
 */
class ToastWrapper {
    var text: String? = null // toast 展示的字符串
    var res: Int? = null //toast 展示的resourceId
    var showSuccess: Boolean = false // 展示成功的标志位
    var showError:Boolean = false  // 展示失败的标志位
}

/**
 * @param init 带有接收者的函数类型的参数 = (A)->B
 */
fun toast(init: ToastWrapper.()-> Unit) {
    val wrap = ToastWrapper()
    wrap.init()
    execute(wrap)
}

private fun execute(wrap: ToastWrapper) {
    var taost: Toast?=null
    wrap.text?.let {
        taost = toast(it)
    }
    wrap.res?.let {
        taost = toast(it)
    }
    if (wrap.showSuccess) {
        taost?.withSuccIcon()
    } else if (wrap.showError) {
        taost?.withErrorIcon()
    }
    taost?.show()
}

/*
使用 DSL 的方式展示一个错误的提示：
这里使用的 DSL 是对链式调用的进一步封装。当然，有人会更喜欢链式调用，也有人会更喜欢 DSL。
*/
fun main() {
    //DSL
    toast {
        res = R.string.connect_exception_error
        showError = true  //  showSuccess 和 showError 一般只需要一个显示 true 即可
    }
    //链式调用
    toast(R.string.connect_exception_error).withErrorIcon().show()
}

