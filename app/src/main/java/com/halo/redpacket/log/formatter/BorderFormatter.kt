package com.halo.redpacket.log.formatter

import com.halo.redpacket.log.Formatter
import com.halo.redpacket.log.printer.LoggerPrinter

/**
 * BorderFormatter 能够展示带边框的日志格式。
 */
class BorderFormatter : Formatter{

    override fun top(): String {
        return LoggerPrinter.BR + LoggerPrinter.TOP_BORDER + LoggerPrinter.BR + LoggerPrinter.HORIZONTAL_DOUBLE_LINE
    }

    override fun middle(): String {
        return LoggerPrinter.BR + LoggerPrinter.MIDDLE_BORDER + LoggerPrinter.BR
    }

    override fun bottom(): String {
        return LoggerPrinter.BR + LoggerPrinter.BOTTOM_BORDER + LoggerPrinter.BR
    }

    override fun spliter(): String {
        return LoggerPrinter.HORIZONTAL_DOUBLE_LINE
    }
}