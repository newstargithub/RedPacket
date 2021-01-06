package com.halo.redpacket.widget.dialog.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.ViewStub
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import com.halo.redpacket.R
import com.halo.redpacket.widget.alpha.XUIAlphaLinearLayout

class BottomSheetItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : XUIAlphaLinearLayout(context, attrs, defStyleAttr) {

    private var mAppCompatImageView: AppCompatImageView? = null
    private lateinit var mTextView: TextView
    private var mSubScript: ViewStub? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        mAppCompatImageView = findViewById(R.id.grid_item_image)
        mSubScript = findViewById(R.id.grid_item_subscript)
        mTextView = findViewById(R.id.grid_item_title)
    }

    @NonNull
    override fun toString(): String {
        return mTextView.text.toString()
    }
}