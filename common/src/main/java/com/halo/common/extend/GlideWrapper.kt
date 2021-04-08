package com.halo.redpacket.extend

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.load.Transformation
import com.halo.redpacket.R

/**
 * 使用 DSL 封装 Glide 框架
 */
class GlideWrapper {
    var url: String? = null // 图片的url
    var imageView: ImageView? = null // 需要加载图片的imageView控件
    var placeHolder: Int = R.drawable.shape_default_rec_bg //占位符
    var error: Int = R.drawable.shape_default_rec_bg //错误提示符
    var transformer: Transformation<Bitmap>? = null //图像转换
}

/**
 * 带有接收者的函数类型的参数 init: GlideWrapper.()-> Unit
 */
fun load(init: GlideWrapper.() -> Unit) {
    val wrap = GlideWrapper()
    wrap.init()
    execute(wrap)
}

private fun execute(wrap: GlideWrapper) {
    wrap.imageView?.let {
        val request = it.get(wrap.url).placeholder(wrap.placeHolder).error(wrap.error)
        if (wrap.transformer != null) {
            val transformer = wrap.transformer
            request.transform(transformer)
        }
        request.into(it)
    }
}

fun main() {
    // 加载某张图片，并让它呈现出圆角矩形的效果：
    /*load {
        url = image_url
        imageView = holder.itemView.iv_game
        transformer = RoundedCornersTransformation(DisplayUtil.dp2px(context, 10f), 0, centerCrop = false)
    }*/
}