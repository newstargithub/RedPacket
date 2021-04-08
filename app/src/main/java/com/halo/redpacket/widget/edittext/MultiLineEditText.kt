package com.halo.redpacket.widget.edittext

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.halo.redpacket.R
import com.halo.redpacket.extend.dp2px
import com.halo.redpacket.extend.sp2px
import com.halo.redpacket.extend.then
import com.halo.redpacket.util.ThemeUtils
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * 多行计数输入框
 * ignoreCnOrEn 为false的时候
 * 1个中文算1个
 * 2个英文算1个
 * 另外：如：只有一个英文时也算1个
 *
 * @author XUE
 * @since 2019/3/22 13:46
 */
class MultiLineEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.MultiLineEditTextStyle) : LinearLayout(context, attrs, defStyleAttr) {


    private lateinit var mTvInputNumber: TextView
    private lateinit var mEtInput: EditText

    /**
     * 最大输入的字符数
     */
    private var mMaxCount = 0
    /**
     * 输入提示文字
     */
    private var mHintText :CharSequence? = null

    /**
     * 提示文字的颜色
     */
    private var mHintTextColor: Int = 0
    /**
     * 是否忽略中英文差异
     */
    private var mIgnoreCnOrEn = true
    /**
     * 输入内容
     */
    private var mContentText: CharSequence? = null

    /**
     * 输入框文字大小
     */
    private var mContentSize: Float = 0F
    /**
     * 输入框文字颜色
     */
    private var mContentTextColor = 0
    /**
     * 输入框高度
     */
    private var mContentViewHeight = 0
    /**
     * 输入框高度是否是固定高度，默认是true
     */
    private var mIsFixHeight = false
    /**
     * 输入框padding
     */
    private var mContentPadding = 0
    /**
     * 输入框背景
     */
    private var mContentBackground: Drawable? = null

    /**
     * 是否显示剩余数目
     */
    private var mIsShowSurplusNumber = true

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.MultiLineEditText, defStyleAttr, 0)
        mMaxCount = typeArray.getInteger(R.styleable.MultiLineEditText_mlet_maxCount, 240)
        mIgnoreCnOrEn = typeArray.getBoolean(R.styleable.MultiLineEditText_mlet_ignoreCnOrEn, true)
        mHintText = typeArray.getText(R.styleable.MultiLineEditText_mlet_hintText)
        mHintTextColor = typeArray.getColor(R.styleable.MultiLineEditText_mlet_hintTextColor, ThemeUtils.resolveColor(getContext(), R.attr.xui_config_color_hint_text))
        mContentPadding = typeArray.getDimensionPixelSize(R.styleable.MultiLineEditText_mlet_contentPadding, context.dp2px(10))
        mContentBackground = typeArray.getDrawable(R.styleable.MultiLineEditText_mlet_contentBackground)
        mContentText = typeArray.getText(R.styleable.MultiLineEditText_mlet_contentText)
        mContentSize = typeArray.getDimension(R.styleable.MultiLineEditText_mlet_contentTextSize, context.sp2px(14).toFloat())
        mContentTextColor = typeArray.getColor(R.styleable.MultiLineEditText_mlet_contentTextColor, ThemeUtils.resolveColor(getContext(), R.attr.xui_config_color_input_text))
        mContentViewHeight = typeArray.getDimensionPixelSize(R.styleable.MultiLineEditText_mlet_contentViewHeight, context.dp2px(140))
        mIsFixHeight = typeArray.getBoolean(R.styleable.MultiLineEditText_mlet_isFixHeight, true)
        mIsShowSurplusNumber = typeArray.getBoolean(R.styleable.MultiLineEditText_mlet_showSurplusNumber, false)
        typeArray.recycle()
    }

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.xui_layout_multiline_edittext, this)
        mEtInput = view.findViewById<EditText>(R.id.mlet_input)
        mTvInputNumber = view.findViewById<TextView>(R.id.mlet_number)
        if (background == null) {
            setBackgroundResource(R.drawable.mlet_selector_bg)
        }
        mEtInput.addTextChangedListener(mTextWatcher)
        mEtInput.hint = mHintText
        mEtInput.setHintTextColor(mHintTextColor)
        mEtInput.setText(mContentText)
        mEtInput.setPadding(mContentPadding, mContentPadding, mContentPadding, 0)
        if (mContentBackground != null) {
            mEtInput.background = mContentBackground
        }
        mEtInput.setTextColor(mContentTextColor)
        mEtInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContentSize)
        if (mIsFixHeight) {
            mEtInput.height = mContentViewHeight
        } else {
            mEtInput.minHeight = mContentViewHeight
        }
        /**
         * 配合 mTvInputNumber xml的 android:focusable="true"
         * android:focusableInTouchMode="true"
         * 在mlet_input设置完文本后
         * 不给mlet_input 焦点
         */
        mTvInputNumber.requestFocus()
        //init
        configCount()
        mEtInput.setSelection(mEtInput.length())
        /**
         * focus后给背景设置Selected
         */
        mEtInput.setOnFocusChangeListener { _, hasFocus ->
            this@MultiLineEditText.isSelected = hasFocus
        }
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            var mEditStart = mEtInput.selectionStart
            var mEditEnd = mEtInput.selectionEnd

            // 先去掉监听器，否则会出现栈溢出
            mEtInput.removeTextChangedListener(this)

            if (mIgnoreCnOrEn) {
                //当输入字符个数超过限制的大小时，进行截断操作
                while (calculateLengthIgnoreCnOrEn(editable.toString()) > mMaxCount) {
                    editable.delete(mEditStart - 1, mEditEnd)
                    mEditStart--
                    mEditEnd--
                }
            } else {
                // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
                while (calculateLength(editable.toString()) > mMaxCount) { // 当输入字符个数超过限制的大小时，进行截断操作
                    editable.delete(mEditStart - 1, mEditEnd)
                    mEditStart--
                    mEditEnd--
                }
            }

            mEtInput.setSelection(mEditStart)

            // 恢复监听器
            mEtInput.addTextChangedListener(this)

            //update
            configCount()
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }
    }

    private fun configCount() {
        if (mIgnoreCnOrEn) {
            val nowCount = calculateLengthIgnoreCnOrEn(mEtInput.text.toString())
            updateCount(nowCount)
        } else {
            val nowCount = calculateLength(mEtInput.text.toString())
            updateCount(nowCount)
        }
    }

    private fun updateCount(nowCount: Int) {
        mIsShowSurplusNumber.then({
            mTvInputNumber.text = "${(mMaxCount - nowCount)} / $mMaxCount"
        }, {
            mTvInputNumber.text = "$nowCount / $mMaxCount"
        })
    }

    private fun calculateLength(text: String): Int {
        var len = 0f
        for (c in text.toCharArray()) {
            if (c.toInt() > 0 && c.toInt() < 127) {
                len += 0.5f
            } else {
                len ++
            }
        }
        return round(len).toInt()
    }

    private fun calculateLengthIgnoreCnOrEn(text: String?): Int {
        return text?.length ?: 0
    }

    fun getEditText(): EditText {
        return mEtInput
    }

    fun getCountTextView(): TextView {
        return mTvInputNumber
    }

    private fun calculateContentLength(content: String): Long {
        return (if (mIgnoreCnOrEn) calculateLengthIgnoreCnOrEn(content) else calculateLength(content)).toLong()
    }

    /**
     * 设置填充内容
     *
     * @param content
     */
    fun setContentText(content: String?) {
        mContentText = if (content != null && calculateContentLength(content) > mMaxCount) {
            content.subSequence(0, getSubStringIndex(content))
        } else {
            content
        }
        mEtInput.setText(mContentText)
    }

    private fun getSubStringIndex(content: String): Int {
        if (!mIgnoreCnOrEn) {
            var len = 0.0
            for (i in 0 until content.length) {
                val tmp = content[i].toInt()
                if (tmp > 0 && tmp < 127) {
                    len += 0.5
                } else {
                    len++
                }
                if (len.roundToInt() == mMaxCount) {
                    return i + 1
                }
            }
        }
        return mMaxCount
    }

    /**
     * 获取输入的内容
     *
     * @return
     */
    fun getContentText(): CharSequence? {
        mContentText = mEtInput.text.toString()
        return mContentText
    }

    fun setHintText(hintText: String) {
        mHintText = hintText
        mEtInput.hint = hintText
    }

    fun setContentTextSize(size: Int) {
        mEtInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
    }

    fun setContentTextColor(color: Int) {
        mEtInput.setTextColor(color)
    }

    fun setHintColor(color: Int) {
        mEtInput.setHintTextColor(color)
    }

    fun getHintText(): CharSequence? {
        mHintText = if (mEtInput.hint == null) "" else mEtInput.hint.toString()
        return mHintText
    }

    fun setMaxCount(max_count: Int): MultiLineEditText {
        mMaxCount = max_count
        configCount()
        return this
    }

    fun setIgnoreCnOrEn(ignoreCnOrEn: Boolean): MultiLineEditText {
        mIgnoreCnOrEn = ignoreCnOrEn
        configCount()
        return this
    }

    fun setIsShowSurplusNumber(isShowSurplusNumber: Boolean): MultiLineEditText {
        mIsShowSurplusNumber = isShowSurplusNumber
        configCount()
        return this
    }

    /**
     * 输入的内容是否为空
     *
     * @return
     */
    fun isEmpty(): Boolean {
        return TextUtils.isEmpty(getContentText())
    }

    /**
     * 输入的内容是否不为空
     *
     * @return
     */
    fun isNotEmpty(): Boolean {
        return !TextUtils.isEmpty(getContentText())
    }

    init {
        initAttrs(context, attrs, defStyleAttr)
        initView()
    }
}