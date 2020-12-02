package com.halo.redpacket.log.printer

interface LoggerPrinter {

    companion object {
        val JSON_INDENT: Int = 0
        val MIDDLE_BORDER: String = "-----------------------------"
        val TOP_BORDER: String = "-----------------------------"
        val BOTTOM_BORDER: String = "-----------------------------"
        val BR = "\n"
        val HORIZONTAL_DOUBLE_LINE = "-----------------------------\n-----------------------------"
    }

}