package com.halo.common.dialog

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.halo.common.R
import com.halo.redpacket.extend.screenHeight
import com.halo.redpacket.extend.toast

/**
 * DialogFragment基类
 * @desc :
 */
abstract class BaseNoModelDialogFragment<DB : ViewDataBinding?> : DialogFragment() {
    val TAG = this::class.simpleName

    private var mCanceledOnTouchOutside: Boolean = false

    protected var dataBinding: DB? = null
    protected lateinit var mContext: Context
    protected var mActivity: FragmentActivity? = null
    private var loadingDialog: LoadingDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate<DB>(inflater, layoutId(), container, false)
        return dataBinding!!.root
    }

    /**
     * 显示错误提示框
     * ViewModel层发生了错误
     */
    protected open fun showError(obj: Any?) {
        obj?.let {
            toast(it.toString())
        }
    }

    /**
     * 初始化要加载的布局资源ID
     */
    protected abstract fun layoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.XUIDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity = getActivity()
        initView()
        initData()
    }

    override fun onStart() {
        super.onStart()
        setWindowLayout()
    }

    private fun setWindowLayout() {
        dialog?.apply {
            window?.apply {
                val params = this.attributes
                params.width = windowWidth()
//                params.height = windowHeight()
                params.gravity = windowGravity()
                attributes = params
            }
            setCanceledOnTouchOutside(mCanceledOnTouchOutside)
        }
    }

    fun setCanceledOnTouchOutside(cancel: Boolean) {
        mCanceledOnTouchOutside = cancel
        dialog?.setCanceledOnTouchOutside(cancel)
    }

    /**
     * 初始化视图
     */
    protected abstract fun initView()

    /**
     * 初始化数据
     */
    protected abstract fun initData()

    /**
     * 显示用户等待框
     *
     * @param msg 提示信息
     */
    protected fun showDialog(msg: String?) {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog?.setLoadingMsg(msg)
        } else {
            loadingDialog = LoadingDialog(mContext)
            loadingDialog?.setLoadingMsg(msg)
            loadingDialog?.show()
        }
    }

    /**
     * 隐藏等待框
     */
    protected fun dismissDialog() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
            loadingDialog = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dataBinding?.unbind()
    }

    /**
     * 窗口宽度
     * @return
     */
    protected fun windowWidth(): Int {
        return WindowManager.LayoutParams.WRAP_CONTENT
    }

    /**
     * 窗口高度
     * @return
     */
    protected fun windowHeight(): Int {
        return mContext.screenHeight
    }

    /**
     * 窗口contentView布局对齐方式
     * @return
     */
    protected fun windowGravity(): Int {
        return Gravity.CENTER
    }

    fun show(manager: FragmentManager) {
        try {
            show(manager, TAG)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}