package com.halo.redpacket.mvvm

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.halo.redpacket.base.BaseActivity
import com.halo.redpacket.base.BaseFragment
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * 委托的方式来创建 ViewModel
 * BaseActivity、BaseFragment 的扩展函数 viewModelDelegate() 支持在 Activity、Fragment 中生成对应的 ViewModel。
 * 使用委托代理的方式来创建之前例子的 ViewModel：
 class AboutusActivity : BaseActivity() {
    private val viewModel by viewModelDelegate(AboutUsViewModel::class)
 ...
 }
 */
class ViewModelDelegate<out T : BaseViewModel>(private var clazz: KClass<T>, private var factory: ViewModelProvider.Factory?, private var fromActivity: Boolean) {
    private var viewModel: T? = null

    operator fun getValue(thisRef: BaseActivity, property: KProperty<*>) = buildViewModel(activity = thisRef)

    operator fun getValue(thisRef: BaseFragment, property: KProperty<*>) = if (fromActivity) {
        buildViewModel(activity = thisRef.activity as? BaseActivity
                ?: throw IllegalStateException("Activity must be as BaseActivity"))
    } else buildViewModel(fragment = thisRef)

    /**
     * 创建ViewModel
     */
    private fun buildViewModel(activity: BaseActivity?= null, fragment: BaseFragment?= null): T {
        if (viewModel != null) return viewModel!!
        activity?.let {
            viewModel = ViewModelProviders.of(it, factory).get(clazz.java)
        } ?: fragment?.let {
            viewModel = ViewModelProviders.of(it, factory).get(clazz.java)
        } ?: throw IllegalStateException("Activity or Fragment is null! ")
        return viewModel!!
    }
}

/**
 * 扩展方法
 */
fun <T : BaseViewModel> BaseActivity.viewModelDelegate(clazz: KClass<T>, factory: ViewModelProvider.Factory? = null)
        = ViewModelDelegate<T>(clazz, factory,true)

/**
 *
 * @param fromActivity 默认为true，viewModel生命周期默认跟activity相同 by aaron 2018/7/24
 */
fun <T : BaseViewModel> BaseFragment.viewModelDelegate(clazz: KClass<T>, factory: ViewModelProvider.Factory? = null, fromActivity: Boolean = true)
        = ViewModelDelegate<T>(clazz, factory,fromActivity)