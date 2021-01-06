package com.halo.redpacket.widget.edittext

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import androidx.appcompat.widget.AppCompatEditText
import com.halo.redpacket.R
import com.halo.redpacket.util.ResUtils
import com.halo.redpacket.util.ResUtils.isRtl

/**
 * 带删除按钮的输入框
 *
 * @author xuexiang
 * @since 2019/1/14 下午10:06
 */
class ClearEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.ClearEditTextStyle): AppCompatEditText(context, attrs, defStyleAttr), View.OnFocusChangeListener, TextWatcher {

    /**
     * 增大点击区域
     */
    private var mExtraClickArea: Int = 0
    /**
     * 删除按钮的引用
     */
    private var mClearDrawable: Drawable? = null

    init {
        initAttrs(context, attrs, defStyleAttr)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText, defStyleAttr, 0)
        mClearDrawable = ResUtils.getDrawableAttrRes(context, typedArray, R.styleable.ClearEditText_cet_clearIcon)
        val iconSize = typedArray.getDimensionPixelSize(R.styleable.ClearEditText_cet_clearIconSize, 0)
        typedArray.recycle()

        if (mClearDrawable == null) {
            //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
            mClearDrawable = compoundDrawablesRelative[2]
            if (mClearDrawable == null) {
                mClearDrawable = ResUtils.getDrawable(context, R.drawable.xui_ic_default_clear_btn)
            }
        }
        mClearDrawable?.apply {
            if (iconSize != 0) {
                setBounds(0, 0, iconSize, iconSize)
            } else {
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            }
        }
        setClearIconVisible(false)
        onFocusChangeListener = this
        addTextChangedListener(this)
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    private fun setClearIconVisible(visible: Boolean) {
        var end = if (visible) mClearDrawable  else null
        setCompoundDrawablesRelative(compoundDrawablesRelative[0], compoundDrawablesRelative[1], end, compoundDrawablesRelative[3])
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (compoundDrawablesRelative[2] != null) {
            if (event.action == MotionEvent.ACTION_UP) {
                if (isTouchable(event)) {
                    setText("")
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isTouchable(event: MotionEvent): Boolean {
        val x = event.x
        if (isRtl()) {
             return x > paddingLeft - mExtraClickArea
                     && x < paddingLeft + mClearDrawable!!.intrinsicWidth + mExtraClickArea
        } else {
             return x > width - paddingRight - mClearDrawable!!.intrinsicWidth - mExtraClickArea
                     && x < width - paddingRight + mExtraClickArea
        }
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            setClearIconVisible(!text.isNullOrEmpty())
        } else {
            setClearIconVisible(false)
        }
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        setClearIconVisible(text.isNotEmpty())
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        setClearIconVisible(false)
    }

    override fun afterTextChanged(s: Editable) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    /**
     * 设置晃动动画
     */
    fun setShakeAnimation() {
        animation = shakeAnimation(5)
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    fun shakeAnimation(counts: Int): Animation {
        val ta = TranslateAnimation(0f, 10f, 0f, 0f)
        ta.interpolator = CycleInterpolator(counts.toFloat())
        ta.duration = 1000
        return ta
    }
}