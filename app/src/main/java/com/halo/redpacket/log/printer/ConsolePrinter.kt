package com.halo.redpacket.log.printer

import android.util.Log
import com.halo.redpacket.log.Formatter
import com.halo.redpacket.log.LogLevel
import com.halo.redpacket.log.Printer
import com.halo.redpacket.log.formatter.BorderFormatter

class ConsolePrinter(override val formatter: Formatter = BorderFormatter()) : Printer{

    override fun println(logLevel: LogLevel, tag: String, msg: String) {
        when(logLevel) {
            LogLevel.ERROR ->
                Log.e(tag, msg)
            LogLevel.WARN ->
                Log.w(tag, msg)
            LogLevel.INFO ->
                Log.i(tag, msg)
            LogLevel.DEBUG ->
                Log.d(tag, msg)
        }
    }
}