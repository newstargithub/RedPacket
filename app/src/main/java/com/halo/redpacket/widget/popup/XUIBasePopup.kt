package com.halo.redpacket.widget.popup

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.annotation.LayoutRes

/**
 * 基础BasePopup
 * Constructor.
 *
 * @param context Context
 */
abstract class XUIBasePopup(private val mContext: Context) {

    //设置消失的监听
    private var mDismissListener: PopupWindow.OnDismissListener? = null
    private var mRootViewWrapper: View? = null
    private var mRootView: View? = null
    private val mNeedCacheSize: Boolean = true
    protected var mWindowHeight: Int = 0
    protected var mWindowWidth: Int = 0
    protected val mScreenSize: Point = Point()
    private var mBackground: Drawable? = null
    private var mWindowManager: WindowManager
    protected var mPopupWindow: PopupWindow

    init {
        mPopupWindow = PopupWindow(mContext)
        mPopupWindow.setTouchInterceptor { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                mPopupWindow.dismiss()
            }
            return@setTouchInterceptor false
        }
        mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * On dismiss
     */
    protected fun onDismiss() {

    }

    /**
     * On PreShow
     */
    protected fun onPreShow() {

    }

    fun show(view: View) {
        show(view, view)
    }

    /**
     * @param parent     a parent view to get the {@link View#getWindowToken()} token from
     * @param anchorView
     */
    fun show(parent: View, anchorView: View) {
        preShow()
        mWindowManager.defaultDisplay.getSize(mScreenSize)
        if (mWindowWidth == 0 || mWindowHeight == 0 || !mNeedCacheSize) {
            measureWindowSize()
        }
        val point = onShow(anchorView)
        mPopupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, point.x, point.y)
        // 在相关的View被移除时，window也自动移除。避免当Fragment退出后，Fragment中弹出的PopupWindow还存在于界面上。  
        anchorView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener{
            override fun onViewDetachedFromWindow(v: View?) {
                if (isShowing()) {
                    mPopupWindow.dismiss()
                }
            }

            override fun onViewAttachedToWindow(v: View?) {
            }
        })
    }

    abstract fun onShow(anchorView: View) : Point

    private fun measureWindowSize() {
        mRootView?.let {
            it.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            mWindowWidth = it.measuredWidth
            mWindowHeight = it.measuredHeight
        }
    }

    /**
     * Set background drawable.
     *
     * @param background Background drawable
     */
    fun setBackgroundDrawable(drawable: Drawable) {
        mBackground = drawable
    }

    private fun preShow() {
        if (mRootViewWrapper == null) {
            throw IllegalStateException("setContentView was not called with a view to display.")
        }
        onPreShow()
        if (mBackground == null) {
            mPopupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } else {
            mPopupWindow.setBackgroundDrawable(mBackground)
        }
        mPopupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        mPopupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        mPopupWindow.isTouchable = true
        mPopupWindow.isFocusable = true
        mPopupWindow.isOutsideTouchable = true
        mPopupWindow.contentView = mRootViewWrapper
    }

    /**
     * Set content view.
     *
     * @param root Root view
     */
    open fun setContentView(root: View?) {
        if (root == null) {
            throw IllegalStateException("setContentView was not called with a view to display.")
        }
        mRootView = root
        mRootViewWrapper = RootView(mContext).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            addView(root)
        }
        mPopupWindow.contentView = mRootViewWrapper
        mPopupWindow.setOnDismissListener {
            onDismiss()
            mDismissListener?.onDismiss()
        }
    }

    /**
     * Set content view.
     *
     * @param layoutResID Resource id
     */
    fun setContentView(@LayoutRes layoutResID: Int) {
        val view = LayoutInflater.from(mContext).inflate(layoutResID, null)
        setContentView(view)
    }

    fun isShowing(): Boolean {
        return mPopupWindow.isShowing
    }

    fun getContext(): Context {
        return mContext
    }

    fun dismiss() {
        mPopupWindow.dismiss()
    }

    inner class RootView : FrameLayout {
        constructor(context: Context?) : super(context!!)
        constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

        override fun onConfigurationChanged(newConfig: Configuration) {
            if (mPopupWindow.isShowing) {
                mPopupWindow.dismiss()
            }
            this@XUIBasePopup.onConfigurationChanged(newConfig)
        }
    }

    protected fun onConfigurationChanged(newConfig: Configuration) {

    }
}