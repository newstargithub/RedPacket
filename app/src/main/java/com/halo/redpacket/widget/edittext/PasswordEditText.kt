package com.halo.redpacket.widget.edittext

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.halo.redpacket.R
import com.halo.redpacket.extend.dp2px
import com.halo.redpacket.extend.then
import com.halo.redpacket.util.ResUtils
import kotlin.math.roundToInt

/**
 * 支持显示密码的输入框
 *
 * @author xuexiang
 * @since 2019/1/14 下午10:08
 */
class PasswordEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.PasswordEditTextStyle) : AppCompatEditText(context, attrs, defStyleAttr) {

    /**
     * 触摸事件捕获中
     */
    private var mHandlingHoverEvent: Boolean = false

    /**
     * 密码可见
     */
    private var mPasswordVisible: Boolean = false

    /**
     * 显示图标
     */
    private var mShowingIcon: Boolean = false

    private var mSetErrorCalled: Boolean = false

    /**
     * 密码转换方式
     */
    private var mTransformationMethod: PasswordTransformationMethod? = null

    /**
     * 是否触摸显示密码
     */
    private var mHoverShowsPw: Boolean = false

    /**
     * 隐藏密码图标
     */
    private lateinit var mHidePwDrawable: Drawable
    /**
     * 显示密码图标
     */
    private lateinit var mShowPwDrawable: Drawable

    companion object {
        val ALPHA_ICON_ENABLED: Int = (255 * 0.54f).roundToInt()
        val ALPHA_ICON_DISABLED: Int = (255 * 0.38f).roundToInt()
    }

    /**
     * 增大点击区域
     */
    private var mExtraClickArea: Int = 0


    init {
        initAttrs(context, attrs, defStyleAttr)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        mExtraClickArea = context.dp2px(20)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText, defStyleAttr, 0)
        val useNonMonospaceFont: Boolean
        val enableIconAlpha: Boolean
        try {
            mShowPwDrawable = ResUtils.getDrawableAttrRes(context, typedArray, R.styleable.PasswordEditText_pet_iconShow)
                    ?: ResUtils.getVectorDrawable(context, R.drawable.pet_icon_visibility_24dp)
            mHidePwDrawable = ResUtils.getDrawableAttrRes(context, typedArray, R.styleable.PasswordEditText_pet_iconHide)
                    ?: ResUtils.getVectorDrawable(context, R.drawable.pet_icon_visibility_off_24dp)
            mHoverShowsPw = typedArray.getBoolean(R.styleable.PasswordEditText_pet_hoverShowsPw, false)
            useNonMonospaceFont = typedArray.getBoolean(R.styleable.PasswordEditText_pet_nonMonospaceFont, false)
            enableIconAlpha = typedArray.getBoolean(R.styleable.PasswordEditText_pet_enableIconAlpha, true)
            val isAsteriskStyle = typedArray.getBoolean(R.styleable.PasswordEditText_pet_isAsteriskStyle, false)
            mTransformationMethod = isAsteriskStyle.then(AsteriskPasswordTransformationMethod.getInstance(), PasswordTransformationMethod.getInstance())
        } finally {
            typedArray.recycle()
        }
        if (enableIconAlpha) {
            mHidePwDrawable.alpha = ALPHA_ICON_ENABLED
            mShowPwDrawable.alpha = ALPHA_ICON_DISABLED
        }
        if (useNonMonospaceFont) {
            typeface = Typeface.DEFAULT
        }
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length > 0) {
                    if (mSetErrorCalled) {
                        mSetErrorCalled = false
                        setCompoundDrawablesRelative(null, null, null, null)
                        showPasswordVisibilityIndicator(true)
                    }
                    if (!mShowingIcon) {
                        showPasswordVisibilityIndicator(true)
                    }
                } else {
                    // hides the indicator if no text inside text field
                    mPasswordVisible = false
                    handlePasswordInputVisibility()
                    showPasswordVisibilityIndicator(false)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        handlePasswordInputVisibility()
    }

    /**
     * This method is called when restoring the state (e.g. on orientation change)
     */
    private fun handlePasswordInputVisibility() {
        val selectionStart = selectionStart
        val selectionEnd = selectionEnd
        transformationMethod = (!mPasswordVisible).then(mTransformationMethod)
        setSelection(selectionStart, selectionEnd)
    }

    /**
     * @param shouldShowIcon 是否显示密码图标
     */
    private fun showPasswordVisibilityIndicator(shouldShowIcon: Boolean) {
        if (shouldShowIcon) {
            val drawable = mPasswordVisible.then(mShowPwDrawable, mHidePwDrawable)
            mShowingIcon = true
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
        } else {
            mShowingIcon = false
            // reset drawable
            setCompoundDrawablesRelative(null, null, null, null)
        }
    }

    fun setExtraClickAreaSize(extraClickArea: Int): PasswordEditText {
        mExtraClickArea = extraClickArea
        return this
    }

    /**
     * 设置密码输入框的样式
     *
     * @param transformationMethod
     * @return
     */
    fun setPasswordTransformationMethod(transformationMethod: PasswordTransformationMethod): PasswordEditText {
        mTransformationMethod = transformationMethod
        return this
    }

    /**
     * 设置密码输入框的样式
     *
     * @param isAsteriskStyle
     * @return
     */
    fun setIsAsteriskStyle(isAsteriskStyle: Boolean): PasswordEditText {
        mTransformationMethod = if (isAsteriskStyle) {
            AsteriskPasswordTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        return this
    }

    private fun isRtl(): Boolean {
        return layoutDirection == View.LAYOUT_DIRECTION_RTL
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return SavedState(superState!!, mShowingIcon, mPasswordVisible)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.getSuperState())
        mShowingIcon = savedState.isShowingIcon
        mPasswordVisible = savedState.isPasswordVisible
        handlePasswordInputVisibility()
        showPasswordVisibilityIndicator(mShowingIcon)
    }

    override fun setError(error: CharSequence?) {
        super.setError(error)
        mSetErrorCalled = true
    }

    override fun setError(error: CharSequence?, icon: Drawable?) {
        super.setError(error, icon)
        mSetErrorCalled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mShowingIcon) {
            return super.onTouchEvent(event)
        } else {
            val touchable = isTouchable(event)
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (mHoverShowsPw) {
                        if (touchable) {
                            togglePasswordIconVisibility()
                            // prevent keyboard from coming up
                            event.action = MotionEvent.ACTION_CANCEL
                            mHandlingHoverEvent = true
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (mHandlingHoverEvent || touchable) {
                        togglePasswordIconVisibility()
                        // prevent keyboard from coming up
                        event.action = MotionEvent.ACTION_CANCEL
                        mHandlingHoverEvent = false
                    }
                }
            }
            return super.onTouchEvent(event)
        }
    }

    /**
     * This method toggles the visibility of the icon and takes care of switching the input type
     * of the view to be able to see the password afterwards.
     *
     *
     * This method may only be called if there is an icon visible
     */
    private fun togglePasswordIconVisibility() {
        mPasswordVisible = !mPasswordVisible
        handlePasswordInputVisibility()
        showPasswordVisibilityIndicator(true)
    }

    private fun isTouchable(event: MotionEvent): Boolean {
        return if (isRtl()) {
            event.x > paddingLeft - mExtraClickArea && event.x < paddingLeft + mExtraClickArea + mShowPwDrawable.intrinsicWidth
        } else {
            event.x > width - paddingRight - mShowPwDrawable.intrinsicWidth - mExtraClickArea && event.x < width - paddingRight + mExtraClickArea
        }
    }

    /**
     * Convenience class to save / restore the state of icon.
     */
    protected class SavedState : BaseSavedState {
        val isShowingIcon: Boolean
        val isPasswordVisible: Boolean

        constructor(superState: Parcelable, sI: Boolean, pV: Boolean) : super(superState) {
            isShowingIcon = sI
            isPasswordVisible = pV
        }

        constructor(`in`: Parcel) : super(`in`) {
            isShowingIcon = `in`.readByte().toInt() != 0
            isPasswordVisible = `in`.readByte().toInt() != 0
        }

        override fun writeToParcel(destination: Parcel, flags: Int) {
            super.writeToParcel(destination, flags)
            destination.writeByte((if (isShowingIcon) 1 else 0).toByte())
            destination.writeByte((if (isPasswordVisible) 1 else 0).toByte())
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }


}