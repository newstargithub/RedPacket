package com.halo.redpacket.extend

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.halo.redpacket.R
import com.halo.redpacket.base.GlideApp
import com.halo.redpacket.base.GlideRequest
import com.halo.redpacket.util.DisplayUtil
import com.halo.redpacket.util.glide.RoundedCornersTransformation


fun ImageView.get(url: String?): GlideRequest<Drawable> = GlideApp.with(context).load(url)
fun ImageView.get(url: Drawable?): GlideRequest<Drawable> = GlideApp.with(context).load(url)


/**
 * 占位符矩形
 */
fun ImageView.load(url: String?, placeholderRes: Int = R.drawable.shape_default_rec_bg,
                   errorRes: Int = R.drawable.shape_default_rec_bg) {
    get(url).placeholder(placeholderRes).error(errorRes).into(this)
}

/**
 * 占位符圆角矩形
 */
fun ImageView.loadRound(url: String?, placeholderRes: Int = R.drawable.shape_default_rec_bg,
                        errorRes: Int = R.drawable.shape_default_rec_bg) {
    get(url).placeholder(placeholderRes)
            .error(errorRes)
            .transform(RoundedCornersTransformation(DisplayUtil.dp2px(context, 10f), 0))
            .into(this)
}

/**
 * 占位符圆形
 */
fun ImageView.loadCircle(url: Drawable?) {
    get(url).placeholder(R.drawable.shape_default_circle_bg)
            .apply(RequestOptions.circleCropTransform())
            .error(R.drawable.shape_default_circle_bg)
            .into(this)
}

fun ImageView.loadCircle(url: String?) {
    get(url).placeholder(R.drawable.shape_default_circle_bg)
            .apply(RequestOptions.circleCropTransform())
            .error(R.drawable.shape_default_circle_bg)
            .into(this)
}