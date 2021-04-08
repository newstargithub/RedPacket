package com.halo.redpacket.util

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import java.io.File

/**
 * @author Zhouxin
 * @date :2021/1/19
 * @description:
 */
class DirUtil {
    companion object {
        /**
         * 外部缓存
         * /storage/emulated/0/Android/data/packageName/cache
         */
        fun getExternalCacheDir(context: Context): File? {
            return context.getExternalCacheDir()
        }

        /**
         * /data/user/0/packageName
         */
        @RequiresApi(Build.VERSION_CODES.N)
        fun getDataDir(context: Context): File? {
            return context.dataDir
        }

        /**
         * /data/cache
         */
        fun getDownloadCacheDirectory(): File? {
            return Environment.getDownloadCacheDirectory()
        }

        /**
         * /storage/emulated/0/Android/data/packageName/files
         */
        fun getExternalFilesDir(context: Context, type: String? ): File? {
            return context.getExternalFilesDir(type)
        }


    }
}