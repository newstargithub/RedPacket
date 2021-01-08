package com.halo.redpacket.ui.fragment.location.model

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

object LocateState {
    const val LOCATING = 123
    const val SUCCESS = 132
    const val FAILURE = 321

    @IntDef(*[SUCCESS, FAILURE])
    @Retention(RetentionPolicy.SOURCE)
    annotation class State
}