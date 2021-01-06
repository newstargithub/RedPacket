package com.halo.redpacket.widget.spinner

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.halo.redpacket.R
import com.halo.redpacket.extend.gone
import com.halo.redpacket.extend.screenHeight
import com.halo.redpacket.extend.visible
import com.halo.redpacket.util.ResUtils
import com.halo.redpacket.util.ThemeUtils

class DropDownMenu @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.DropDownMenuStyle)
    : LinearLayout(context, attrs, defStyleAttr) {

    private var mContentView: View? = null

    private var mContentLayoutId: Int = 0

    /**
     * 弹出菜单父布局
     */
    private var mPopupMenuViews: FrameLayout? = null

    /**
     * 遮罩半透明View，点击可关闭DropDownMenu
     */
    private var mMaskView: View? = null
    /**
     * tabMenuView里面选中的tab位置，-1表示未选中
     */
    private var mCurrentTabPosition: Int = -1
    /**
     * 底部容器，包含popupMenuViews，maskView
     */
    private lateinit var mContainerView: FrameLayout
    /**
     * 顶部菜单布局
     */
    private lateinit var mTabMenuView: LinearLayout
    /**
     * tab字体大小
     */
    private var mMenuTextSize: Int = 0
    /**
     * tab字体水平padding
     */
    private var mMenuTextPaddingVertical: Int = 0
    /**
     * tab字体水平padding
     */
    private var mMenuTextPaddingHorizontal: Int = 0
    /**
     * tab未选中颜色
     */
    private var mMenuTextUnselectedColor: Int = 0
    /**
     * tab选中颜色
     */
    private var mMenuTextSelectedColor: Int = 0
    /**
     * 遮罩颜色
     */
    private var mMaskColor: Int = 0
    /**
     * 分割线的Margin
     */
    private var mDividerMargin: Int = 0
    /**
     * 分割线宽度
     */
    private var mDividerWidth: Int = 0
    /**
     * 选择菜单的高度/屏幕高度 占比
     */
    private var mMenuHeightPercent: Float = 0.5f
    /**
     * tab选中图标
     */
    private lateinit var mMenuSelectedIcon: Drawable
    /**
     * tab未选中图标
     */
    private lateinit var mMenuUnselectedIcon: Drawable
    /**
     * 分割线颜色
     */
    private var mDividerColor: Int = 0



    init {
        orientation = VERTICAL
        initAttr(context, attrs, defStyleAttr)
    }

    private fun initAttr(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        //为DropDownMenu添加自定义属性
        val array = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu)
        mContentLayoutId = array.getResourceId(R.styleable.DropDownMenu_ddm_contentLayoutId, -1)
        mDividerColor = array.getColor(R.styleable.DropDownMenu_ddm_dividerColor, ThemeUtils.resolveColor(getContext(), R.attr.xui_config_color_separator_dark))
        mDividerWidth = array.getDimensionPixelSize(R.styleable.DropDownMenu_ddm_dividerWidth, ResUtils.getDimensionPixelSize(R.dimen.default_ddm_divider_width))
        mDividerMargin = array.getDimensionPixelSize(R.styleable.DropDownMenu_ddm_dividerMargin, ResUtils.getDimensionPixelSize(R.dimen.default_ddm_divider_margin))
        val underlineColor = array.getColor(R.styleable.DropDownMenu_ddm_underlineColor, ThemeUtils.resolveColor(getContext(), R.attr.xui_config_color_separator_light))
        val underlineHeight = array.getDimensionPixelSize(R.styleable.DropDownMenu_ddm_underlineHeight, ResUtils.getDimensionPixelSize(R.dimen.default_ddm_underline_height))
        val menuBackgroundColor = array.getColor(R.styleable.DropDownMenu_ddm_menuBackgroundColor, Color.WHITE)
        mMaskColor = array.getColor(R.styleable.DropDownMenu_ddm_maskColor, ResUtils.getColor(R.color.default_ddm_mask_color))
        mMenuTextSelectedColor = array.getColor(R.styleable.DropDownMenu_ddm_menuTextSelectedColor, ThemeUtils.resolveColor(getContext(), R.attr.colorAccent))
        mMenuTextUnselectedColor = array.getColor(R.styleable.DropDownMenu_ddm_menuTextUnselectedColor, ThemeUtils.resolveColor(getContext(), R.attr.xui_config_color_content_text))
        mMenuTextPaddingHorizontal = array.getDimensionPixelSize(R.styleable.DropDownMenu_ddm_menuTextPaddingHorizontal, ResUtils.getDimensionPixelSize(R.dimen.default_ddm_menu_text_padding_horizontal))
        mMenuTextPaddingVertical = array.getDimensionPixelSize(R.styleable.DropDownMenu_ddm_menuTextPaddingVertical, ResUtils.getDimensionPixelSize(R.dimen.default_ddm_menu_text_padding_vertical))
        mMenuTextSize = array.getDimensionPixelSize(R.styleable.DropDownMenu_ddm_menuTextSize, ResUtils.getDimensionPixelSize(R.dimen.default_ddm_menu_text_size))
        mMenuUnselectedIcon = ResUtils.getDrawableAttrRes(context, array, R.styleable.DropDownMenu_ddm_menuUnselectedIcon) ?:
                ResUtils.getVectorDrawable(getContext(), R.drawable.ddm_ic_arrow_down)
        mMenuSelectedIcon = ResUtils.getDrawableAttrRes(context, array, R.styleable.DropDownMenu_ddm_menuSelectedIcon) ?:
                ResUtils.getVectorDrawable(getContext(), R.drawable.ddm_ic_arrow_up)
        mMenuHeightPercent = array.getFloat(R.styleable.DropDownMenu_ddm_menuHeightPercent, mMenuHeightPercent)
        array.recycle()


        //初始化tabMenuView并添加到tabMenuView
        mTabMenuView = LinearLayout(context)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mTabMenuView.orientation = HORIZONTAL
        mTabMenuView.setBackgroundColor(menuBackgroundColor)
        mTabMenuView.layoutParams = params
        addView(mTabMenuView, 0)

        //为tabMenuView添加下划线
        val underLine = View(context)
        underLine.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, underlineHeight)
        underLine.setBackgroundColor(underlineColor)
        addView(underLine, 1)

        //初始化containerView并将其添加到DropDownMenu
        mContainerView = FrameLayout(context)
        mContainerView.layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(mContainerView, 2)
    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts
     * @param popupViews
     */
    fun setDropDownMenu(tabTexts: Array<String>, popupViews: List<View>) {
        setDropDownMenu(arrayListOf(*tabTexts), popupViews)
    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts
     * @param popupViews
     */
    fun setDropDownMenu(tabTexts: List<String>, popupViews: List<View>) {
        require(mContentLayoutId != -1) {
            "mContentLayoutId == -1, You need to set properties app:ddm_contentLayoutId"
        }
        setDropDownMenu(tabTexts, popupViews, View.inflate(context, mContentLayoutId, null))
    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts
     * @param popupViews
     * @param contentView 内容显示区域
     */
    fun setDropDownMenu(tabTexts: Array<String>, popupViews: List<View>, contentView: View) {
        setDropDownMenu(arrayListOf(*tabTexts), popupViews, contentView)
    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts
     * @param popupViews
     * @param contentView 内容显示区域
     */
    private fun setDropDownMenu(tabTexts: List<String>, popupViews: List<View>, contentView: View) {
        require(tabTexts.size == popupViews.size) { "params not match, tabTexts.size() should be equal popupViews.size()" }

        for (i in tabTexts.indices) {
            addTab(tabTexts, i)
        }
        mContentView = contentView
        mContainerView.addView(contentView, 0)

        mMaskView = View(context).apply {
            setBackgroundColor(mMaskColor)
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setOnClickListener {
                closeMenu()
            }
            gone()
        }
        mContainerView.addView(mMaskView, 1)
        if (mContainerView.getChildAt(2) != null) {
            mContainerView.removeViewAt(2)
        }

        mPopupMenuViews = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (context.screenHeight * mMenuHeightPercent).toInt())
            gone()
        }
        mContainerView.addView(mPopupMenuViews, 2)

        for (i in popupViews.indices) {
            popupViews[i].layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mPopupMenuViews!!.addView(popupViews[i], i)
        }
    }

    private fun addTab(tabTexts: List<String>, index: Int) {
        val tab = TextView(context)
        tab.setSingleLine()
        tab.ellipsize = TextUtils.TruncateAt.END
        tab.gravity = Gravity.CENTER
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMenuTextSize.toFloat())
        tab.layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f)
        tab.setTextColor(mMenuTextUnselectedColor)
        setArrowIconEnd(tab, mMenuUnselectedIcon)
        tab.text = tabTexts[index]
        tab.setPadding(mMenuTextPaddingHorizontal, mMenuTextPaddingVertical, mMenuTextPaddingHorizontal, mMenuTextPaddingVertical)
        //添加点击事件
        tab.setOnClickListener {
            switchMenu(tab)
        }
        mTabMenuView.addView(tab)
        //添加分割线
        if (index < tabTexts.size - 1) {
            val view = View(context)
            val params = LayoutParams(mDividerWidth, ViewGroup.LayoutParams.MATCH_PARENT)
            params.topMargin = mDividerMargin
            params.bottomMargin = mDividerMargin
            view.setBackgroundColor(mDividerColor)
            view.layoutParams = params
            mTabMenuView.addView(view)
        }
    }

    /**
     * 切换菜单
     *
     * @param target
     */
    fun switchMenu(target: View) {
        var i = 0
        while (i < mTabMenuView.childCount) {
            if (target == mTabMenuView.getChildAt(i)) {
                if (mCurrentTabPosition == i) {
                    closeMenu()
                } else {
                    //展示菜单
                    if (mCurrentTabPosition == -1) {
                        mPopupMenuViews?.visible()
                        mPopupMenuViews?.animation = AnimationUtils.loadAnimation(context, R.anim.ddm_menu_in)
                        mMaskView?.visible()
                        mMaskView?.animation = AnimationUtils.loadAnimation(context, R.anim.ddm_mask_in)
                        mPopupMenuViews?.getChildAt(i / 2)?.visible()
                    } else {
                        mPopupMenuViews?.getChildAt(i / 2)?.visible()
                    }
                    mCurrentTabPosition = i
                    (mTabMenuView.getChildAt(i) as TextView).setTextColor(mMenuTextSelectedColor)
                    setArrowIconEnd((mTabMenuView.getChildAt(i) as TextView), mMenuSelectedIcon)
                }
            } else {
                (mTabMenuView.getChildAt(i) as TextView).setTextColor(mMenuTextUnselectedColor)
                setArrowIconEnd((mTabMenuView.getChildAt(i) as TextView), mMenuUnselectedIcon)
                mPopupMenuViews?.getChildAt(i / 2)?.gone()
            }
            i += 2
        }
    }

    private fun setArrowIconEnd(tab: TextView, icon: Drawable) {
        tab.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icon, null)
    }

    /**
     * 设置tab菜单文字
     *
     * @param text
     */
    fun setTabMenuText(text: String?) {
        if (mCurrentTabPosition != -1) {
            (mTabMenuView.getChildAt(mCurrentTabPosition) as TextView).text = text
        }
    }

    /**
     * 设置tab菜单是否可点击
     *
     * @param clickable
     */
    fun setTabMenuClickable(clickable: Boolean) {
        var i = 0
        while (i < mTabMenuView.childCount) {
            mTabMenuView.getChildAt(i).isClickable = clickable
            i += 2
        }
    }

    /**
     * 关闭菜单
     */
    fun closeMenu() {
        if (mCurrentTabPosition != -1) {
            val view = (mTabMenuView.getChildAt(mCurrentTabPosition) as TextView)
            view.setTextColor(mMenuTextUnselectedColor)
            setArrowIconEnd(view, mMenuUnselectedIcon)
            mPopupMenuViews?.gone()
            mPopupMenuViews?.animation = AnimationUtils.loadAnimation(context, R.anim.ddm_menu_out)
            mMaskView?.gone()
            mMaskView?.animation = AnimationUtils.loadAnimation(context, R.anim.ddm_menu_out)
            mCurrentTabPosition = -1
        }
    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    fun isShowing(): Boolean {
        return mCurrentTabPosition != -1
    }

    /**
     * @return 内容页
     */
    fun getContentView(): View? {
        return mContentView
    }


}