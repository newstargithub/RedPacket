package com.halo.redpacket.mvvm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.halo.redpacket.bean.DialogBean
import com.halo.redpacket.model.RetrofitManager
import com.halo.redpacket.model.api.APIService
import com.halo.redpacket.mvvm.lifecycle.DialogLiveData
import io.reactivex.disposables.CompositeDisposable

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

    /**
     * 管理RxJava请求
     */
    private var compositeDisposable: CompositeDisposable? = null

    /**
     * 用来通知 Activity／Fragment 是否显示等待Dialog
     */
    protected var showDialog: DialogLiveData<DialogBean> = DialogLiveData()

    /**
     * 当ViewModel层出现错误需要通知到Activity／Fragment
     */
    protected var error = MutableLiveData<Any>()

    open fun getShowDialog(owner: LifecycleOwner, observer: Observer<DialogBean>) {
        showDialog.observe(owner, observer)
    }

    open fun getError(owner: LifecycleOwner, observer: Observer<Any?>) {
        error.observe(owner, observer)
    }

    /**
     * ViewModel销毁同时也取消请求
     */
    override fun onCleared() {
        super.onCleared()
        if (compositeDisposable != null) {
            compositeDisposable?.dispose()
            compositeDisposable = null
        }
    }

}