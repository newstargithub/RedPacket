package com.halo.redpacket.log

import com.halo.redpacket.log.handler.BaseHandler
import com.halo.redpacket.log.handler.BundleHandler
import com.halo.redpacket.log.printer.ConsolePrinter
import java.util.*

object L {
    private var handlers = LinkedList<BaseHandler>()
    private lateinit var firstHandler: BaseHandler
    private val printers = mutableSetOf<Printer>()

    init {
        printers.add(ConsolePrinter())// 默认添加 ConsolePrinter
        handlers.apply {
            add(StringHandler())
            add(CollectionHandler())
            add(MapHandler())
            add(BundleHandler())
            add(IntentHandler())
            add(UriHandler())
            add(ThrowableHandler())
            add(ReferenceHandler())
            add(ObjectHandler())
        }
        val len = handlers.size
        for (i in 0 until len) {
            if (i > 0) {
                handlers[i-1].setNextHandle(handlers[i])
            }
        }
        firstHandler = handlers[0]
    }

    /**
     * 将任何对象转换成json字符串进行打印
     */
    @JvmStatic
    fun json(obj: Any?) {
        if (obj == null) {
            d("object is null")
            return
        }
        firstHandler.handleObj(obj)
    }

    /**
     * 自定义 Handler 来解析 Object
     */
    @JvmStatic
    fun addCustomerHandler(handler: BaseHandler): L {
        val size = handlers.size
        return addCustomerHandler(handler, size)
    }

    fun addCustomerHandler(handler: BaseHandler, index: Int): L {
        handlers.add(index, handler)

        val len = handlers.size

        for (i in 0 until len) {
            if (i > 0) {
                handlers[i - 1].setNextHandle(handlers[i])
            }
        }
        return this
    }

    fun printers() = printers\


    fun d(tag: String?, msg: String?) {
        firstHandler.handleObj(msg)
    }

    fun d(msg: String?) {
        firstHandler.handleObj(msg)
    }

    fun i(msg: String?) {

    }

    fun i(tag: String?, msg: String?) {
        firstHandler.handleObj(msg)
    }

    fun w(msg: String?) {

    }

    fun e(msg: String?) {

    }
}

fun String?.e() = L.e(this)

fun String?.w() = L.w(this)

fun String?.i() = L.i(this)

fun String?.d() = L.d(this)

fun Any?.json()  = L.json(this)

typealias msgFunction = () -> String

fun L.i(msgFun: msgFunction) = i(msgFun.invoke())

fun L.i(tag: String?, msg: msgFunction) = i(tag, msg.invoke())

enum class LogLevel {
    ERROR, WARN, INFO, DEBUG
}