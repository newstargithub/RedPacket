package com.halo.redpacket.util.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.nio.ByteBuffer
import java.security.MessageDigest

class RoundedCornersTransformation(val topRadius: Int, val bottomRadius: Int) : BitmapTransformation() {
    private val ID = "com.halo.redpacket.util.glide.RoundedCornersTransformation"
    private val ID_BYTES = ID.toByteArray(Key.CHARSET)

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
        val radiusData = ByteBuffer.allocate(4).putInt(topRadius).putInt(bottomRadius).array()
        messageDigest.update(radiusData)
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return TransformationUtils.roundedCorners(pool, toTransform, topRadius.toFloat(), topRadius.toFloat(), bottomRadius.toFloat(), bottomRadius.toFloat())
    }
}