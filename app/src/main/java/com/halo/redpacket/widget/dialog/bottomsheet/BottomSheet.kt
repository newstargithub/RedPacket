package com.halo.redpacket.widget.dialog.bottomsheet

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseArray
import android.view.*
import android.view.animation.*
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.IntegerRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.halo.redpacket.R
import com.halo.redpacket.extend.gone
import com.halo.redpacket.extend.screenHeight
import com.halo.redpacket.extend.screenWidth
import com.halo.redpacket.extend.visible
import com.halo.redpacket.util.ThemeUtils
import kotlinx.android.synthetic.main.xui_bottom_sheet_grid.view.*
import kotlinx.android.synthetic.main.xui_bottom_sheet_grid_item.view.*
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


/**
 * BottomSheet 在 {@link Dialog} 的基础上重新定制了 {@link #show()} 和 {@link #hide()} 时的动画效果, 使 {@link Dialog} 在界面底部升起和降下。
 * <p>
 * 提供了以下两种面板样式:
 * <ul>
 * <li>列表样式, 使用 {@link BottomSheet.BottomListSheetBuilder} 生成。</li>
 * <li>宫格类型, 使用 {@link BottomSheet.BottomGridSheetBuilder} 生成。</li>
 * </ul>
 * </p>
 */
class BottomSheet(context: Context) : Dialog(context, R.style.BottomSheet) {

    // 持有 ContentView，为了做动画
    private var mContentView: View? = null
    private var mIsAnimating = false
    var mOnBottomSheetShowListener: OnBottomSheetShowListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.let {
            it.decorView.setPadding(0, 0, 0, 0)

            // 在底部，宽度撑满
            val params = it.attributes
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.BOTTOM or Gravity.CENTER

            val screenWidth = context.screenWidth
            val screenHeight = context.screenHeight
            params.width = Math.min(screenWidth, screenHeight)
            it.attributes = params
        }
        setCanceledOnTouchOutside(true)
    }

    override fun setContentView(layoutResID: Int) {
        mContentView = LayoutInflater.from(context).inflate(layoutResID, null)
        super.setContentView(mContentView!!)
    }

    override fun setContentView(view: View) {
        mContentView = view
        super.setContentView(view)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        mContentView = view
        super.setContentView(view, params)
    }

    fun getContentView(): View? = mContentView

    /**
     * BottomSheet升起动画
     */
    private fun animateUp() {
        mContentView?.let {
            val ta = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f)
            val aa = AlphaAnimation(0f, 1f)
            val set = AnimationSet(true)
            set.addAnimation(ta)
            set.addAnimation(aa)
            set.duration = ANIMATION_DURATION
            set.interpolator = DecelerateInterpolator()
            set.fillAfter = true
            it.startAnimation(set)
        }
    }

    /**
     * BottomSheet降下动画
     */
    private fun animateDown() {
        mContentView?.let {
            val ta = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f)
            val aa = AlphaAnimation(1f, 0f)
            val set = AnimationSet(true)
            set.addAnimation(ta)
            set.addAnimation(aa)
            set.duration = ANIMATION_DURATION
            set.interpolator = DecelerateInterpolator()
            set.fillAfter = true
            set.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    mIsAnimating = false
                    /**
                     * Bugfix： Attempting to destroy the window while drawing!
                     */
                    it.post {
                        // java.lang.IllegalArgumentException: View=com.android.internal.policy.PhoneWindow$DecorView{22dbf5b V.E...... R......D 0,0-1080,1083} not attached to window manager
                        // 在dismiss的时候可能已经detach了，简单try-catch一下
                        try {
                            super@BottomSheet.dismiss()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onAnimationStart(animation: Animation?) {
                    mIsAnimating = true
                }
            })
            it.startAnimation(set)
        }
    }

    override fun show() {
        super.show()
        animateUp()
        mOnBottomSheetShowListener?.onShow()
    }

    override fun dismiss() {
        if (mIsAnimating) {
            return
        }
        animateDown()
    }

    /**
     * 生成列表类型的 {@link BottomSheet} 对话框。
     */
    class BottomListSheetBuilder(private val context: Context, private val needRightMark: Boolean = false) {

        private var mContainerView: ListView? = null
        private val mOnBottomDialogDismissListener: OnBottomSheetShowListener? = null
        private var mIsCenter: Boolean = false
        private var mTitle: String? = null
        private var mOnBottomSheetShowListener: OnBottomSheetShowListener? = null
        private var mOnSheetItemClickListener: OnSheetItemClickListener? = null
        private var mCheckedIndex: Int = -1
        private var items = arrayListOf<BottomSheetListItemData>()
        private lateinit var mDialog: BottomSheet
        private var mAdapter: BaseAdapter? = null
        private var mHeaderViews = arrayListOf<View>()

        /**
         * 设置要被选中的 Item 的下标。
         * <p>
         * 注意:仅当 {@link #mNeedRightMark} 为 true 时才有效。
         */
        fun setCheckedIndex(checkedIndex: Int): BottomListSheetBuilder {
            mCheckedIndex = checkedIndex
            return this
        }

        /**
         * @param textAndTag Item 的文字内容，同时会把内容设置为 tag。
         */
        fun addItem(textAndTag: String): BottomListSheetBuilder {
            items.add(BottomSheetListItemData(textAndTag, textAndTag))
            return this
        }

        /**
         * @param image      icon Item 的 icon。
         * @param textAndTag Item 的文字内容，同时会把内容设置为 tag。
         */
        fun addItem(image: Drawable, textAndTag: String): BottomListSheetBuilder {
            items.add(BottomSheetListItemData(textAndTag, textAndTag, image))
            return this
        }

        /**
         * @param text Item 的文字内容。
         * @param tag  item 的 tag。
         */
        fun addItem(text: String, tag: String): BottomListSheetBuilder {
            items.add(BottomSheetListItemData(text, tag))
            return this
        }

        /**
         * @param imageRes Item 的图标 Resource。
         * @param text     Item 的文字内容。
         * @param tag      Item 的 tag。
         */
        fun addItem(imageRes: Int, text: String, tag: String): BottomListSheetBuilder {
            val image = if (imageRes != 0) ContextCompat.getDrawable(context, imageRes) else null
            items.add(BottomSheetListItemData(text, tag, image))
            return this
        }

        /**
         * @param imageRes    Item 的图标 Resource。
         * @param text        Item 的文字内容。
         * @param tag         Item 的 tag。
         * @param hasRedPoint 是否显示红点。
         */
        fun addItem(imageRes: Int, text: String, tag: String, hasRedPoint: Boolean): BottomListSheetBuilder {
            val image = if (imageRes != 0) ContextCompat.getDrawable(context, imageRes) else null
            items.add(BottomSheetListItemData(text, tag, image, hasRedPoint))
            return this
        }

        /**
         * @param imageRes    Item 的图标 Resource。
         * @param text        Item 的文字内容。
         * @param tag         Item 的 tag。
         * @param hasRedPoint 是否显示红点。
         * @param disabled    是否显示禁用态。
         */
        fun addItem(imageRes: Int, text: String, tag: String, hasRedPoint: Boolean, disabled: Boolean): BottomListSheetBuilder {
            val image = if (imageRes != 0) ContextCompat.getDrawable(context, imageRes) else null
            items.add(BottomSheetListItemData(text, tag, image, hasRedPoint, disabled))
            return this
        }

        fun setOnBottomSheetShowListener(onBottomSheetShowListener: OnBottomSheetShowListener): BottomListSheetBuilder {
            mOnBottomSheetShowListener = onBottomSheetShowListener
            return this
        }

        fun addHeaderView(view: View?): BottomListSheetBuilder {
            if (view != null) {
                mHeaderViews.add(view)
            }
            return this
        }

        fun setTitle(title: String): BottomListSheetBuilder {
            mTitle = title
            return this
        }

        fun setTitle(resId: Int): BottomListSheetBuilder {
            mTitle = context.getResources().getString(resId)
            return this
        }

        /**
         * 设置文字是否居中对齐
         * @param isCenter
         * @return
         */
        fun setIsCenter(isCenter: Boolean): BottomListSheetBuilder {
            mIsCenter = isCenter
            return this
        }

        fun setOnSheetItemClickListener(listener: OnSheetItemClickListener): BottomListSheetBuilder {
            mOnSheetItemClickListener = listener
            return this
        }

        fun build(): BottomSheet {
            mDialog = BottomSheet(context)
            val contentView = buildViews()
            mDialog.setContentView(contentView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            if (mOnBottomDialogDismissListener != null) {
                mDialog.mOnBottomSheetShowListener = mOnBottomSheetShowListener
            }
            return mDialog
        }

        private fun buildViews(): View {
            val wrapperView = LayoutInflater.from(context).inflate(getContentViewLayoutId(), null)
            val title = wrapperView.findViewById<TextView>(R.id.title)
            mContainerView = wrapperView.findViewById<ListView>(R.id.listview)
            if (TextUtils.isEmpty(mTitle)) {
                title.gone()
            } else {
                title.visible()
                title.text = mTitle
            }
            if (mIsCenter) {
                title.gravity = Gravity.CENTER
            }
            if (mHeaderViews.isNotEmpty()) {
                for (view in mHeaderViews) {
                    mContainerView?.addHeaderView(view)
                }
            }
            if (needToScroll()) {
                mContainerView?.layoutParams?.height = getListMaxHeight()
                mDialog.mOnBottomSheetShowListener = object : OnBottomSheetShowListener {
                    override fun onShow() {
                        mContainerView?.setSelection(mCheckedIndex)
                    }
                }
            }
            mAdapter = ListAdapter(mIsCenter)
            mContainerView?.adapter = mAdapter
            return wrapperView
        }

        private fun needToScroll(): Boolean {
            val itemHeight = ThemeUtils.resolveDimension(context, R.attr.xui_bottom_sheet_list_item_height)
            var totalHeight = itemHeight * items.size
            if (mHeaderViews.isNotEmpty()) {
                for (head in mHeaderViews) {
                    head.measure(0 ,0)
                    totalHeight += head.measuredHeight
                }
            }
            if (!TextUtils.isEmpty(mTitle)) {
                totalHeight += ThemeUtils.resolveDimension(context, R.attr.xui_bottom_sheet_title_height)
            }
            return totalHeight > getListMaxHeight();
        }

        /**
         * 注意:这里只考虑List的高度,如果有title或者headerView,不计入考虑中
         */
        private fun getListMaxHeight(): Int {
            return (context.screenHeight * 0.5).toInt()
        }

        fun notifyDataSetChanged() {
            mAdapter?.notifyDataSetChanged()
            if (needToScroll()) {
                mContainerView?.run {
                    layoutParams.height = getListMaxHeight()
                    setSelection(mCheckedIndex)
                }
            }
        }

        private fun getContentViewLayoutId(): Int {
            return R.layout.xui_bottom_sheet_list
        }

        class ViewHolder(val view: View) {
            val imageView = view.findViewById<ImageView>(R.id.bottom_dialog_list_item_img);
            val textView = view.findViewById<TextView>(R.id.bottom_dialog_list_item_title);
            var markView = view.findViewById<View>(R.id.bottom_dialog_list_item_mark_view_stub);
            val redPoint = view.findViewById<ImageView>(R.id.bottom_dialog_list_item_point);
        }

        private inner class ListAdapter(val isCenter: Boolean) : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val item = getItem(position)
                var viewHolder: ViewHolder
                var view: View
                if (convertView == null) {
                    view = LayoutInflater.from(context).inflate(R.layout.xui_bottom_sheet_list_item, parent, false)
                    viewHolder = ViewHolder(view)
                    if (isCenter) {
                        viewHolder.textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        viewHolder.textView.gravity = Gravity.CENTER
                    }
                    view.tag = viewHolder
                } else {
                    view = convertView
                    viewHolder = convertView.tag as ViewHolder
                }
                bindData(viewHolder, item, position)
                return view
            }

            private fun bindData(holder: ViewHolder, item: BottomSheetListItemData, position: Int) {
                if (item.image != null) {
                    holder.imageView.visible()
                    holder.imageView.setImageDrawable(item.image)
                } else {
                    holder.imageView.gone()
                }
                holder.textView.text = item.text
                if (item.hasRedPoint) {
                    holder.redPoint.visible()
                } else {
                    holder.redPoint.gone()
                }
                if (item.isDisabled) {
                    holder.textView.isEnabled = false
                    holder.view.isEnabled = false
                } else {
                    holder.textView.isEnabled = true
                    holder.view.isEnabled = true
                }
                if (needRightMark) {
                    if (holder.markView is ViewStub) {
                        holder.markView = (holder.markView as ViewStub).inflate()
                    }
                    if (mCheckedIndex == position) {
                        holder.markView.visible()
                    } else {
                        holder.markView.gone()
                    }
                } else {
                    holder.markView.gone()
                }
                holder.view.setOnClickListener {
                    if (item.hasRedPoint) {
                        item.hasRedPoint = false
                        holder.redPoint.gone()
                    }
                    if (needRightMark) {
                        setCheckedIndex(position)
                        notifyDataSetChanged()
                    }
                    mOnSheetItemClickListener?.onClick(mDialog, holder.view, position, item.tag)
                }
            }

            override fun getItem(position: Int): BottomSheetListItemData {
                return items[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getCount(): Int {
                return items.size
            }
        }
    }

    class BottomSheetListItemData(val text: String,
                                  val tag: String,
                                  val image: Drawable? = null,
                                  var hasRedPoint: Boolean = false,
                                  val isDisabled: Boolean = false
    )


    companion object {
        val ANIMATION_DURATION = 200L
    }

    interface OnBottomSheetShowListener {
        fun onShow()
    }

    interface OnSheetItemClickListener {
        fun onClick(dialog: BottomSheet, itemView: View, position: Int, tag: String?)
    }


    /**
     * 生成宫格类型的 {@link BottomSheet} 对话框。
     */
    class BottomGridSheetBuilder(private val mContext: Context) {

        private var mMiniItemWidth: Int = -1
        private lateinit var mDialog: BottomSheet
        private var mOnSheetItemClickListener: OnSheetItemClickListener? = null
        private val mFirstLineViews = SparseArray<View>()
        private val mSecondLineViews = SparseArray<View>()
        private var mButtonClickListener: View.OnClickListener? = null
        private var mButtonText: CharSequence? = null
        private var mIsShowButton: Boolean = true

        fun addItem(@DrawableRes imageRes: Int, textAndTag: CharSequence, @Style style: Int): BottomGridSheetBuilder {
            return addItem(imageRes, textAndTag, textAndTag, style, 0)
        }

        fun addItem(@DrawableRes imageRes: Int, text: CharSequence, tag: Any, @Style style: Int): BottomGridSheetBuilder {
            return addItem(imageRes, text, tag, style, 0)
        }

        fun addItem(@DrawableRes imageRes: Int, text: CharSequence, tag: Any, @Style style: Int, subscriptRes: Int): BottomGridSheetBuilder {
            val itemView: BottomSheetItemView = createItemView(AppCompatResources.getDrawable(mContext, imageRes), text, tag, subscriptRes)
            return addItem(itemView, style)
        }

        private fun addItem(itemView: BottomSheetItemView, @Style style: Int): BottomGridSheetBuilder {
            when(style) {
                FIRST_LINE -> {
                    mFirstLineViews.append(mFirstLineViews.size(), itemView)
                }
                SECOND_LINE -> {
                    mSecondLineViews.append(mSecondLineViews.size(), itemView)
                }
            }
            return this
        }

        fun setIsShowButton(isShowButton: Boolean): BottomGridSheetBuilder {
            mIsShowButton = isShowButton
            return this
        }

        fun setButtonText(buttonText: CharSequence): BottomGridSheetBuilder {
            mButtonText = buttonText
            return this
        }

        fun setButtonClickListener(buttonClickListener: View.OnClickListener): BottomGridSheetBuilder? {
            mButtonClickListener = buttonClickListener
            return this
        }

        private fun createItemView(drawable: Drawable?, text: CharSequence, tag: Any, subscriptRes: Int): BottomSheetItemView {
            val itemView: BottomSheetItemView = LayoutInflater.from(mContext).inflate(getItemViewLayoutId(), null, false) as BottomSheetItemView
            itemView.grid_item_title.text = text
            itemView.grid_item_image.setImageDrawable(drawable)
            itemView.tag = tag
            itemView.setOnClickListener {
                mOnSheetItemClickListener?.onClick(mDialog, it as BottomSheetItemView)
            }
            if (subscriptRes != 0) {
                val inflate = itemView.grid_item_subscript.inflate()
                (inflate as ImageView).setImageResource(subscriptRes)
            }
            return itemView
        }

        fun setItemVisibility(tag: Any, visibility: Int) {
            var foundView: View? = null
            for (i in 0 until mFirstLineViews.size()) {
                val view = mFirstLineViews[i]
                if (view != null && view.tag == tag) {
                    foundView = view
                    break
                }
            }
            for (i in 0 until mSecondLineViews.size()) {
                val view = mSecondLineViews[i]
                if (view != null && view.tag == tag) {
                    foundView = view
                    break
                }
            }
            if (foundView != null) {
                foundView.visibility = visibility
            }
        }

        fun setOnSheetItemClickListener(listener: OnSheetItemClickListener): BottomGridSheetBuilder {
            mOnSheetItemClickListener = listener
            return this
        }

        fun build(): BottomSheet {
            mDialog = BottomSheet(mContext)
            val contentView = buildViews()
            mDialog.setContentView(contentView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            return mDialog
        }

        private fun buildViews(): View {
            val baseLinearLayout = View.inflate(mContext, getContentViewLayoutId(), null)
            val firstLine = baseLinearLayout.bottom_sheet_first_linear_layout
            val secondLine = baseLinearLayout.bottom_sheet_second_linear_layout
            val mBottomButtonContainer = baseLinearLayout.bottom_sheet_button_container
            val mBottomButton = baseLinearLayout.bottom_sheet_close_button
            val maxItemCountEachLine = Math.max(mFirstLineViews.size(), mSecondLineViews.size())
            val width = Math.min(mContext.screenWidth, mContext.screenHeight)
            val itemWidth = calculateItemWidth(width, maxItemCountEachLine, firstLine.paddingStart, firstLine.paddingEnd)
            addViewsInSection(mFirstLineViews, firstLine, itemWidth)
            addViewsInSection(mSecondLineViews, secondLine, itemWidth)

            val hasFirstLine = mFirstLineViews.size() > 0
            val hasSecondLine = mSecondLineViews.size() > 0
            if (!hasFirstLine) {
                firstLine.visibility = View.GONE
            }
            if (!hasSecondLine) {
                //沒有第二行，有第一行
                if (hasFirstLine) {
                    firstLine.setPadding(
                            firstLine.paddingLeft,
                            firstLine.paddingTop,
                            firstLine.paddingRight,
                            0)
                }
                secondLine.visibility = View.GONE
            }

            if (mBottomButtonContainer != null) {
                if (mIsShowButton) {
                    mBottomButtonContainer.visible()
                    baseLinearLayout.setPadding(baseLinearLayout.paddingLeft, baseLinearLayout.paddingTop,
                            baseLinearLayout.paddingRight, 0)
                } else {
                    mBottomButtonContainer.gone()
                }
                if (!TextUtils.isEmpty(mButtonText)) {
                    mBottomButton.text = mButtonText
                }
                if (mButtonClickListener != null) {
                    mBottomButton.setOnClickListener(mButtonClickListener)
                } else {
                    mBottomButton.setOnClickListener {
                        mDialog.dismiss()
                    }
                }
            }
            return baseLinearLayout
        }

        private fun addViewsInSection(views: SparseArray<View>, parent: LinearLayout, itemWidth: Int) {
            for (i in 0 until views.size()) {
                val itemView = views[i]
                setItemWidth(itemView, itemWidth)
                parent.addView(itemView)
            }
        }

        private fun setItemWidth(itemView: View, itemWidth: Int) {
            var layoutParams: LinearLayout.LayoutParams? = itemView.layoutParams as LinearLayout.LayoutParams?
            if (layoutParams != null) {
                layoutParams.width = itemWidth
            } else {
                layoutParams = LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                itemView.layoutParams = layoutParams
            }
            layoutParams.gravity = Gravity.TOP
        }

        private fun calculateItemWidth(width: Int, countEachLine: Int, paddingStart: Int, paddingEnd: Int): Int {
            if (mMiniItemWidth == -1) {
                mMiniItemWidth = ThemeUtils.resolveDimension(mContext, R.attr.xui_bottom_sheet_grid_item_mini_width)
            }
            val parentSpacing = width - paddingStart - paddingEnd
            var itemWidth = mMiniItemWidth
            // 看是否需要把 Item 拉伸平分 parentSpacing
            if (countEachLine >= 3 && parentSpacing > itemWidth * itemWidth
                    && parentSpacing - itemWidth * itemWidth < itemWidth) {
                val count = parentSpacing / itemWidth
                itemWidth = parentSpacing / count
            }
            // 看是否需要露出半个在屏幕边缘
            if (itemWidth * countEachLine > parentSpacing) {
                val count: Int = (width - paddingStart) / itemWidth
                itemWidth = ((width - paddingStart) / (count + .5f)).toInt()
            }
            return itemWidth
        }

        private fun getContentViewLayoutId(): Int {
            return R.layout.xui_bottom_sheet_grid
        }

        private fun getItemViewLayoutId(): Int {
            return R.layout.xui_bottom_sheet_grid_item
        }

        companion object {
            const val FIRST_LINE: Int = 1
            const val SECOND_LINE: Int = 2
        }

        interface OnSheetItemClickListener {
            fun onClick(dialog: BottomSheet, itemView: BottomSheetItemView)
        }

        @Retention(RetentionPolicy.SOURCE)
        @IntDef(BottomGridSheetBuilder.FIRST_LINE, BottomGridSheetBuilder.SECOND_LINE)
        annotation class Style
    }

}