package com.halo.redpacket.mvvm

import androidx.lifecycle.ViewModel
import com.halo.redpacket.model.RetrofitManager
import com.halo.redpacket.model.api.APIService

/**
 * 方便 ViewModel 的子类使用 apiService。
 *
 * ViewModel 不能持有 View 层的引用。例如当 Activity 重新创建时，如果 ViewModel 并没有被销毁，这样会导致内存泄露。
    但是凡事都有例外，如果需要在 ViewModel 中使用 Context，则可以使用 AndroidViewModel，因为 AndroidViewModel
    包含有 Application Context 。否则请使用 ViewModel，并且 AndroidViewModel 是 ViewModel 的子类。
 */
open class BaseViewModel : ViewModel(){
    // apiService 对象
    var apiService: APIService = RetrofitManager.get().apiService()
}