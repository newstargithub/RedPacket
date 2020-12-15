package com.halo.redpacket.util

import android.content.Context
import com.halo.redpacket.App

class AppUtils {

    companion object {
        fun getApplicationContext(): Context {
            return App.instance
        }
    }

}