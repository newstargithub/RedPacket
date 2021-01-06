package com.halo.redpacket.widget.alpha

import android.view.View
import com.halo.redpacket.R
import com.halo.redpacket.util.ThemeUtils
import java.lang.ref.WeakReference

class XUIAlphaViewHelper(target: View) : IAlphaViewHelper {

    private var mChangeAlphaWhenDisable: Boolean
    private var mChangeAlphaWhenPress: Boolean
    private val mNormalAlpha = 1f
    private var mDisabledAlpha: Float
    private var mPressedAlpha: Float
    private var mTarget: WeakReference<View>

    init {
        mTarget = WeakReference<View>(target)
        mChangeAlphaWhenPress = ThemeUtils.resolveBoolean(target.context, R.attr.xui_switch_alpha_pressed, true)
        mChangeAlphaWhenDisable = ThemeUtils.resolveBoolean(target.context, R.attr.xui_switch_alpha_disabled, true)
        mPressedAlpha = ThemeUtils.resolveFloat(target.context, R.attr.xui_alpha_pressed, 0.5f)
        mDisabledAlpha = ThemeUtils.resolveFloat(target.context, R.attr.xui_alpha_disabled, 0.5f)
    }

    /**
     * @param current the view to be handled, maybe not equal to target view
     * @param pressed
     */
    override fun onPressedChanged(current: View, pressed: Boolean) {
        mTarget.get() ?: return
        if (current.isEnabled) {
            current.alpha = if(mChangeAlphaWhenPress && pressed && current.isClickable) mPressedAlpha else mNormalAlpha
        } else {
            if (mChangeAlphaWhenDisable) {
                current.alpha = mDisabledAlpha
            }
        }
    }

    override fun onEnabledChanged(current: View?, enabled: Boolean) {
        val target = mTarget.get() ?: return
        var alphaForIsEnable: Float
        if (mChangeAlphaWhenDisable) {
            alphaForIsEnable = if(enabled) mNormalAlpha else mDisabledAlpha
        } else {
            alphaForIsEnable = mNormalAlpha
        }
        if (current !== target && target.isEnabled != enabled) {
            target.isEnabled = enabled
        }
        target.alpha = alphaForIsEnable
    }

    override fun setChangeAlphaWhenPress(changeAlphaWhenPress: Boolean) {
        mChangeAlphaWhenPress = changeAlphaWhenPress
    }

    override fun setChangeAlphaWhenDisable(changeAlphaWhenDisable: Boolean) {
        mChangeAlphaWhenDisable = changeAlphaWhenDisable
        val target = mTarget.get()
        if (target != null) {
            onEnabledChanged(target, target.isEnabled)
        }
    }
}