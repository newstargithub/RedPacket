package com.halo.redpacket.util

import android.content.SharedPreferences
import com.halo.redpacket.extend.delegate.boolean
import com.halo.redpacket.extend.delegate.int
import com.halo.redpacket.extend.delegate.string

/**
 * @author Zhouxin
 * @date :2021/3/5
 * @description:
 */
class PrefsHelper(prefs: SharedPreferences) {

    var name by prefs.string("name")

    var password by prefs.string("password")

    var age by prefs.int("age")

    var isForeigner by prefs.boolean("isForeigner")
}