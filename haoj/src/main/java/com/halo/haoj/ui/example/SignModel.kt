package com.iwhalecloud.tobacco.fragment.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.halo.redpacket.mvvm.BaseViewModel
import com.iwhalecloud.tobacco.model.MenuModel

/**
 * @author Zhouxin
 * @date :2021/4/7
 * @description:
 */
class SignModel: BaseViewModel() {

    /**
     * 代签
     */
    fun sign(reason: CharSequence?): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        return liveData
    }

}