package com.halo.redpacket.log

/**
 * Formatter 接口用于格式化日志，便于 Printer 进行打印。
 */
interface Formatter {

    fun top(): String

    fun middle():String

    fun bottom():String

    fun spliter():String
}