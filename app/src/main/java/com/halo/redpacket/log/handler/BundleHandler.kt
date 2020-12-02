package com.halo.redpacket.log.handler

import android.os.Bundle
import com.halo.redpacket.log.Formatter
import com.halo.redpacket.log.L
import com.halo.redpacket.log.LogLevel
import com.halo.redpacket.log.printer.LoggerPrinter
import org.json.JSONObject

class BundleHandler : BaseHandler() {
    override fun handle(obj: Any): Boolean {
        if (obj is Bundle) {
            L.printers().map {
                val s = L.getMethodNames(it.formatter)
                it.println(LogLevel.INFO, this.logTag(), String.format(s, parseString(obj, it.formatter)))
            }
            return true
        }
        return false
    }

    private fun parseString(bundle: Bundle, formatter: Formatter): String {
        var msg = bundle.toJavaClass() + LoggerPrinter.BR + formatter.spliter()
        //之所以使用扩展函数，是为了便于链式调用。
        return msg + JSONObject().parseBundle(bundle)
                .formatJson()
                .let {
                    it.replace("\n", "\n${formatter.spliter()}")
                }
    }
}

/**
 * parseBundle()、formatJSON() 是 JSONObject 的扩展函数。
 */
fun JSONObject.formatJson() = this.toString(LoggerPrinter.JSON_INDENT)

/**
 * 解析 bundle ，并存储到 JSONObject
 */
fun JSONObject.parseBundle(bundle: Bundle): JSONObject {
    bundle.keySet().map {
        val isPrimitiveType = isPrimitiveType(bundle.get(it))
        try {
            if (isPrimitiveType) {
                this.put(it, bundle.get(it))
            } else {
                this.put(it, JSONObject(JSON.toJSONString(bundle.get(it))))
            }
        } catch (e: Exception) {
            L.e("Invalid Json")
        }
    }
    return this
}