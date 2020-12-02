package com.halo.redpacket.log

interface Printer {
    val formatter: Formatter

    fun println(logLevel: LogLevel, tag: String, msg: String)
}