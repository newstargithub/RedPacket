package com.halo.redpacket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.collection.LruCache;

import java.io.IOException;
import java.io.InputStream;

/**
 * Author: zx
 * Date: 2020/7/7
 * Description:
 */
public class BitmapUtil {

    /**
     * 使用 Options.inBitmap 优化，复用图片内存空间
     * @param reuseBitmap 存在了的 Bitmap 对象
     * @param options 要加载的图片配置
     */
    @SuppressLint("NewApi")
    public static void reuseBitmap(Bitmap reuseBitmap, BitmapFactory.Options options) {
        if (canUseForInBitmap(reuseBitmap, options)) {
            options.inMutable = true;
            options.inBitmap = reuseBitmap;
        }
    }

    /**
     * 判断 reuseBitmap 是否可以被复用
     * 因为 Bitmap 的复用有一定的限制：
     * 在 Android 4.4 版本之前，只能重用相同大小的 Bitmap 内存区域；
     * 4.4 之后你可以重用任何 Bitmap 的内存区域，只要这块内存比将要分配内存的 bitmap 大就可以。
     * @param candidate
     * @param targetOptions
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
        int width = targetOptions.outWidth / Math.max(targetOptions.inSampleSize, 1);
        int height = targetOptions.outHeight / Math.max(targetOptions.inSampleSize, 1);
        int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
        return candidate.getAllocationByteCount() >= byteCount;
    }

    /**
     * 每个像素字节数
     * @param config
     * @return
     */
    private static int getBytesPerPixel(Bitmap.Config config) {
        int bytesPerPixel;
        switch (config) {
            case ALPHA_8:
                bytesPerPixel = 1;
                break;
            case RGB_565:
            case ARGB_4444:
                bytesPerPixel = 2;
                break;
            default:
                bytesPerPixel = 4;
                break;
        }
        return bytesPerPixel;
    }

    /**
     * 图片分片显示，显示图片的左上角200*200区域
     * @param context Context
     * @return
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    public Bitmap showRegionImage(Context context) throws IOException {
        InputStream in = context.getAssets().open("rodman3.png");
        BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(in, false);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = decoder.decodeRegion(new Rect(0, 0, 200, 200), options);
        return bitmap;
    }

    private LruCache<String, Bitmap> bitmapCache;

    public void initCache() {
        //指定 LruCache 的最大空间为 20M，当超过 20M 时，LruCache 会根据内部缓存策略将多余 Bitmap 移除。
        int cacheSize = 1024 * 1024 * 20;
        bitmapCache = new LruCache<String, Bitmap>(cacheSize){
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //需要重写此方法来定义每一个Bitmap的大小
                return bitmap.getAllocationByteCount();
            }
        };
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) != null) {
            bitmapCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        return bitmapCache.get(key);
    }
}
