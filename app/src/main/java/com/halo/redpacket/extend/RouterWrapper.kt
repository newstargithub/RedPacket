package com.halo.redpacket.extend

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.halo.redpacket.R

/**
 * @author Zhouxin
 * @date :2021/3/31
 * @description: Kotlin DSL 的实际使用——封装路由框架的使用
 * 对 Router 框架再一次封装，结合带接收者的 Lambda 和 中缀表达式
 */
class RouterWrapper {
    private val paramsContext = ParamsContext()
    var path: String? = null // 路由跳转的路径，表示跳转到某个Activity或某个Fragment
    var enterAnim: Int = R.anim.cp_push_bottom_in
    var exitAnim: Int = R.anim.cp_push_bottom_out
    var context: Context? = null // context 是必传的
    var requestCode: Int = -1

    fun params(init: ParamsContext.() -> Unit) {
        paramsContext.init()
    }

    internal fun getParamsContext() = paramsContext
}

// 定义一个ParamsContext类，用于参数的封装。路由传递的参数是key-value的形式。
class ParamsContext {
    private val map = mutableMapOf<String, Any>()

    /**
     * infix修饰
     */
    infix fun String.to(v: Any) {
        map[this] = v
    }

    @RequiresApi(Build.VERSION_CODES.N)
    internal fun forEach(action: (k: String, v: Any) -> Unit) = map.forEach(action)
}

fun router(init: RouterWrapper.() -> Unit) {
    val wrap = RouterWrapper()
    wrap.init()
    execute(wrap)
}

private fun execute(wrap: RouterWrapper) {
    /* todo 引入Router依赖
    val router = Router.build(wrap.path).anim(wrap.enterAnim, wrap.exitAnim)
    wrap.getParamsContext().forEach { k, v ->  // 按照key-value形式，拼接路由需要传递的参数
        router.with(k, v)
    }
    if (wrap.requestCode >= 0) {  // 设置 requestCode
        router.requestCode(wrap.requestCode)
    }
    router.go(wrap.context)*/
}

/**
 * 通过 DSL 封装后，之前的代码可以这样使用：
 */
fun main() {
    val from = ""
    val value = ""
    val title = "" // getString(R.string.set_pwd)
    val zoneCode = ""
    val phoneNum = ""
    router {
        path = Config.ROUTER_SET_PWD_ACTIVITY
        params {
            Config.BUNDLE_SET_PWD_FROM_KEY to from
            Config.BUNDLE_ORIGIN_VERIFYCODE to value
            Config.BUNDLE_SET_PWD_TITLE_KEY to title
            Config.BUNDLE_ZONE_CODE to zoneCode
            Config.BUNDLE_PHONE_NUM to phoneNum
        }
//        context = this@GetVcodeActivity
    }
}

object Config {
    val BUNDLE_PHONE_NUM: String = "bundle_phone_num"
    val BUNDLE_ZONE_CODE: String = "bundle_zone_code"
    val BUNDLE_SET_PWD_TITLE_KEY: String = "bundle_set_pwd_title_key"
    val BUNDLE_ORIGIN_VERIFYCODE: String = "bundle_origin_verifycode"
    val BUNDLE_SET_PWD_FROM_KEY: String = "bundle_set_pwd_from_key"
    val ROUTER_SET_PWD_ACTIVITY: String = "router_set_pwd_activity"
}
