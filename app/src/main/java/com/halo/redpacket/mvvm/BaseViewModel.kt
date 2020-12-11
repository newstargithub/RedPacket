package com.halo.redpacket.mvvm

import androidx.lifecycle.ViewModel
import com.halo.redpacket.model.RetrofitManager
import com.halo.redpacket.model.api.APIService

/**
 * 方便 ViewModel 的子类使用 apiService。
 */
open class BaseViewModel : ViewModel(){
    var apiService: APIService = RetrofitManager.get().apiService()
}