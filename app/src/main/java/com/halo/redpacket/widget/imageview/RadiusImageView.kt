package com.halo.redpacket.widget.imageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import com.halo.redpacket.R
import com.halo.redpacket.extend.invisible
import kotlin.math.max
import kotlin.math.min

/**
 * 提供为图片添加圆角、边框、剪裁到圆形或其他形状等功能。
 *
 * @author xuexiang
 * @since 2018/12/1 下午11:59
 */
class RadiusImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val mRectF = RectF()
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapPaint: Paint? = null
    private var mNeedResetShader: Boolean = false
    private var mBitmap: Bitmap? = null
    private var mIsSelected: Boolean = false
    private var mCornerRadius: Float = 0F
    private var mIsOval: Boolean = false
    private var mIsCircle: Boolean = false
    private var mIsTouchSelectModeEnabled: Boolean = false
    private var mColorFilter: ColorFilter? = null
    private var mSelectedColorFilter: ColorFilter? = null
    private var mSelectedBorderColor: Int = 0
    private var mSelectedBorderWidth: Int = 0
    private var mSelectedMaskColor: Int = 0
    private var mBorderColor: Int = 0
    private var mBorderWidth: Int = 0
    private lateinit var mMatrix: Matrix
    private lateinit var mBorderPaint: Paint

    init {
        initAttrs(context, attrs, defStyleAttr)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        mBorderPaint = Paint()
        mBorderPaint.isAntiAlias = true //抗锯齿
        mBorderPaint.style = Paint.Style.STROKE //描边

        mMatrix = Matrix()
        scaleType = ScaleType.CENTER_CROP

        val array = context.obtainStyledAttributes(attrs, R.styleable.RadiusImageView, defStyleAttr, 0)
        mBorderWidth = array.getDimensionPixelSize(R.styleable.RadiusImageView_riv_border_width, 0)
        mBorderColor = array.getColor(R.styleable.RadiusImageView_riv_border_color, DEFAULT_BORDER_COLOR)

        mSelectedBorderWidth = array.getDimensionPixelSize(
                R.styleable.RadiusImageView_riv_selected_border_width, mBorderWidth)
        mSelectedBorderColor = array.getColor(R.styleable.RadiusImageView_riv_selected_border_color, DEFAULT_BORDER_COLOR)
        mSelectedMaskColor = array.getColor(R.styleable.RadiusImageView_riv_selected_mask_color, DEFAULT_MASK_COLOR)
        if (mSelectedMaskColor != Color.TRANSPARENT) {
            mSelectedColorFilter = PorterDuffColorFilter(mSelectedMaskColor, PorterDuff.Mode.DARKEN)
        }

        mIsTouchSelectModeEnabled = array.getBoolean(R.styleable.RadiusImageView_riv_is_touch_select_mode_enabled, true)
        mIsCircle = array.getBoolean(R.styleable.RadiusImageView_riv_is_circle, false)
        if (!mIsCircle) {
            mIsOval = array.getBoolean(R.styleable.RadiusImageView_riv_is_oval, false)
        }
        if (!mIsOval) {
            mCornerRadius = array.getDimension(R.styleable.RadiusImageView_riv_corner_radius, 0F)
        }
        array.recycle()
    }

    override fun setScaleType(scaleType: ScaleType?) {
        if (scaleType != ScaleType.CENTER_CROP) {
            throw IllegalArgumentException(String.format("不支持ScaleType %s", scaleType));
        }
        super.setScaleType(scaleType)
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) {
            throw IllegalArgumentException("不支持adjustViewBounds")
        }
        super.setAdjustViewBounds(adjustViewBounds)
    }

    fun setBorderWidth(borderWidth: Int) {
        if (mBorderWidth != borderWidth) {
            mBorderWidth = borderWidth
            invalidate()
        }
    }

    /**
     * 设置边框颜色
     */
    fun setBorderColor(@ColorRes borderColor: Int) {
        if (mBorderColor != borderColor) {
            mBorderColor = borderColor
            invalidate()
        }
    }

    fun setCornerRadius(cornerRadius: Float) {
        if (mCornerRadius != cornerRadius) {
            mCornerRadius = cornerRadius
            if (!mIsCircle && !mIsOval) {
                invalidate()
            }
        }
    }

    fun setSelectedBorderColor(@ColorRes selectedBorderColor: Int) {
        if (mSelectedBorderColor != selectedBorderColor) {
            mSelectedBorderColor = selectedBorderColor
            if (mIsSelected) {
                invalidate()
            }
        }
    }

    fun setSelectedBorderWidth(selectedBorderWidth: Int) {
        if (mSelectedBorderWidth != selectedBorderWidth) {
            mSelectedBorderWidth = selectedBorderWidth
            if (mIsSelected) {
                invalidate()
            }
        }
    }

    fun setSelectedMaskColor(@ColorRes selectedBorderColor: Int) {
        if (mSelectedMaskColor != selectedBorderColor) {
            mSelectedMaskColor = selectedBorderColor
            if (mSelectedBorderColor != Color.TRANSPARENT) {
                mSelectedColorFilter = PorterDuffColorFilter(mSelectedBorderColor, PorterDuff.Mode.DARKEN)
            } else {
                mSelectedColorFilter = null
            }
            if (isSelected) {
                invisible()
            }
        }
    }

    fun setCircle(isCircle: Boolean) {
        if (mIsCircle != isCircle) {
            mIsCircle = isCircle
            requestLayout()
            invalidate()
        }
    }

    fun setOval(isOval: Boolean) {
        var forceUpdate = false
        if (mIsCircle) {
            // 必须先取消圆形
            mIsCircle = false
            forceUpdate = true
        }
        if (mIsOval != isOval || forceUpdate) {
            mIsOval = isOval
            requestLayout()
            invalidate()
        }
    }

    override fun isSelected(): Boolean {
        return mIsSelected
    }

    override fun setSelected(isSelected: Boolean) {
        mIsSelected = isSelected
        invalidate()
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (mColorFilter != cf) {
            mColorFilter = cf
            if (!isSelected) {
                invalidate()
            }
        }
    }

    fun setSelectedColorFilter(selectedColorFilter: ColorFilter) {
        if (mSelectedColorFilter != selectedColorFilter) {
            mSelectedColorFilter = selectedColorFilter
            if (isSelected) {
                invalidate()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        if (mIsCircle) {
            val size = min(width, height)
            setMeasuredDimension(size, size)
        } else {
            if (mBitmap == null) {
                return
            }
            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            val widthWrapContent = widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED
            val heightWrapContent = heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED
            val bmWidth = mBitmap!!.width
            val bmHeight = mBitmap!!.height
            val scaleX = width / bmWidth
            val scaleY = height / bmHeight
            if (widthWrapContent && heightWrapContent) {
                // 保证长宽比
                if (scaleX >= 1 && scaleY >= 1) {
                    setMeasuredDimension(bmWidth, bmHeight)
                    return
                }
                if (scaleX > scaleY) {
                    setMeasuredDimension(bmWidth * scaleY, height)
                } else {
                    setMeasuredDimension(width, bmWidth * scaleX)
                }
            } else if (widthWrapContent) {
                setMeasuredDimension(bmWidth * scaleY, height)
            } else {
                setMeasuredDimension(width, bmWidth * scaleX)
            }
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    private fun setupBitmap() {
        val bitmap = getBitmap()
        if (bitmap == mBitmap) {
            return
        }
        mBitmap = bitmap
        if (mBitmap == null) {
            mColorFilter = null
            invalidate()
            return
        }
        mNeedResetShader = true
        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        if (mBitmapPaint == null) {
            mBitmapPaint = Paint()
            mBitmapPaint?.isAntiAlias = true
        }
        mBitmapPaint?.shader = mBitmapShader
        requestLayout()
        invalidate()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    fun getBitmap(): Bitmap? {
        val drawable = drawable ?: return null
        if (drawable is BitmapDrawable) {
            val bitmap = (drawable.bitmap as BitmapDrawable).bitmap
            val bmWidth = bitmap.width
            val bmHeight = bitmap.height
            if (bmWidth == 0 || bmHeight == 0) {
                return null
            }
            // ensure minWidth and minHeight
            val minScaleX = minimumWidth / bmWidth
            val minScaleY = minimumHeight / bmHeight
            return if (minScaleX > 1 || minScaleY > 1) {
                val scale = max(minScaleX, minScaleY).toFloat()
                val matrix = Matrix()
                matrix.postScale(scale, scale)
                Bitmap.createBitmap(bitmap, 0, 0, bmWidth, bmHeight, matrix, false)
            } else {
                bitmap
            }
        }
        try {
            var bitmap: Bitmap
            if (drawable is ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMEN, COLOR_DRAWABLE_DIMEN, BITMAP_CONFIG)
            } else {
                bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun updateBitmapShader() {
        mMatrix.reset()
        mNeedResetShader = false
        if (mBitmap == null || mBitmapShader == null) {
            return
        }
        mBitmap?.let {
            val bmWidth = it.width
            val bmHeight = it.height
            val scaleX = mWidth / bmWidth
            val scaleY = mHeight / bmHeight
            val scale = max(scaleX, scaleY).toFloat()
            mMatrix.setScale(scale, scale)
            mMatrix.postTranslate(-(scale * bmWidth - mWidth) / 2, -(scale * bmHeight - mHeight) / 2)
            mBitmapShader?.setLocalMatrix(mMatrix)
            mBitmapPaint?.setShader(mBitmapShader)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val width = width
        val height = height
        if (width <= 0 || height <= 0) {
            return
        }
        val borderWidth = if (mIsSelected) mSelectedBorderWidth else mBorderWidth
        if (mBitmap == null || mBitmapShader == null) {
            drawBorder(canvas, borderWidth)
            return
        }
        if (width != mWidth || height != mHeight || mNeedResetShader) {
            mWidth = width
            mHeight = height
            updateBitmapShader()
        }
        drawBitmap(canvas, borderWidth)
        drawBorder(canvas, borderWidth)
    }

    private fun drawBitmap(canvas: Canvas, borderWidth: Int) {
        val halfBorderWidth = borderWidth * 1.0f / 2
        mBitmapPaint?.let { paint->
            paint.colorFilter = if (mIsSelected) mSelectedColorFilter else mColorFilter
            if (mIsCircle) {
                val radius = width / 2f
                canvas.drawCircle(radius, radius, radius- halfBorderWidth, paint)
            } else {
                mRectF.top = halfBorderWidth
                mRectF.left = halfBorderWidth
                mRectF.right = width - halfBorderWidth
                mRectF.bottom = height - halfBorderWidth
                if (mIsOval) {
                    canvas.drawOval(mRectF, paint)
                } else {
                    canvas.drawRoundRect(mRectF, mCornerRadius, mCornerRadius,  paint)
                }
            }
        }
    }

    private fun drawBorder(canvas: Canvas, borderWidth: Int) {
        if (borderWidth <= 0) {
            return
        }
        mBorderPaint.color = if (mIsSelected) mSelectedBorderColor else mBorderColor
        mBorderPaint.strokeWidth = borderWidth.toFloat()
        val halfBorderWidth = borderWidth * 1.0f / 2
        if (mIsCircle) {
            val radius = width / 2f
            canvas.drawCircle(radius, radius, radius- halfBorderWidth, mBorderPaint)
        } else {
            mRectF.top = halfBorderWidth
            mRectF.left = halfBorderWidth
            mRectF.right = width - halfBorderWidth
            mRectF.bottom = height - halfBorderWidth
            if (mIsOval) {
                canvas.drawOval(mRectF, mBorderPaint)
            } else {
                canvas.drawRoundRect(mRectF, mCornerRadius, mCornerRadius,  mBorderPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!this.isClickable) {
            this.isSelected = false
            return super.onTouchEvent(event)
        }
        if (!mIsTouchSelectModeEnabled) {
            return super.onTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> this.isSelected = true
            MotionEvent.ACTION_UP, MotionEvent.ACTION_SCROLL, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_CANCEL -> this.isSelected = false
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    companion object {
        const val DEFAULT_BORDER_COLOR = Color.GRAY
        const val DEFAULT_MASK_COLOR = Color.TRANSPARENT
        const val COLOR_DRAWABLE_DIMEN = 2
        val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
    }
}