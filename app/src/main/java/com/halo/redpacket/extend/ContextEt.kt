package com.halo.redpacket.extend

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*
import com.halo.redpacket.util.ResUtils
import java.io.File

/**
 * 扩展属性，屏幕宽度
 * screen width in pixels
 */
inline val Context.screenWidth
    get() = resources.displayMetrics.widthPixels

/**
 * screen height in pixels
 */
inline val Context.screenHeight
    get() = resources.displayMetrics.heightPixels

/**
 * 网络可用
 * 扩展属性
 */
inline val Context.isNetworkAvailable: Boolean
    @SuppressLint("MissingPermission")
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

/**
 * returns dip(dp) dimension value in pixels
 * @param value dp
 */
fun Context.dp2px(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun Context.dp2px(value: Float): Int = (value * resources.displayMetrics.density).toInt()

/**
 * return sp dimension value in pixels
 * @param value sp
 */
fun Context.sp2px(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()

fun Context.sp2px(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

/**
* converts [px] value into dip or sp
* @param px
*/
fun Context.px2dp(px: Int): Float = px.toFloat() / resources.displayMetrics.density

fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

/**
 * return dimen resource value in pixels
 * @param resource dimen resource
 */
fun Context.dimen2px(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)


fun Context.string(@StringRes id: Int): String = getString(id)

fun Context.color(@ColorRes id: Int): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    resources.getColor(id, theme)
} else {
    resources.getColor(id)
}


/**
 * 获取字符串的数组
 *
 * @param resId
 * @return
 */
fun Context.getStringArray(@ArrayRes resId: Int): Array<String> {
    return resources.getStringArray(resId)
}

/**
 * 获取数字的数组
 *
 * @param resId
 * @return
 */
fun Context.getIntArray(@ArrayRes resId: Int): IntArray {
    return resources.getIntArray(resId)
}

/**
 * 填充布局
 */
fun Context.inflateLayout(@LayoutRes layoutId: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): View
        = LayoutInflater.from(this).inflate(layoutId, parent, attachToRoot)

/**
 * 获取当前app的版本号
 */
fun Context.getAppVersion(): String {
    val appContext = applicationContext
    val manager = appContext.packageManager
    try {
        val info = manager.getPackageInfo(appContext.packageName, 0)
        if (info != null)
            return info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return ""
}

/**
 * 获取当前app的版本号
 */
fun Context.getAppVersionCode(): Int {
    val appContext = applicationContext
    val manager = appContext.packageManager
    try {
        val info = manager.getPackageInfo(appContext.packageName, 0)
        if (info != null)
            return info.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return 0
}

/**
 * 获取应用的包名
 *
 * @param context context
 * @return package name
 */
fun Context.getPackageName(): String = packageName

data class AppInfo(
        val apkPath: String,
        val packageName: String,
        val versionName: String,
        val versionCode: Long,
        val appName: String,
        val icon: Drawable
)

/**
 * @param apkPath 包文件路径
 */
fun Context.getAppInfo(apkPath: String): AppInfo {
    val packageInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_META_DATA) as PackageInfo
    packageInfo.applicationInfo.sourceDir = apkPath
    packageInfo.applicationInfo.publicSourceDir = apkPath

    val packageName = packageInfo.packageName
    val appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
    val versionName = packageInfo.versionName
    val versionCode = packageInfo.versionCode
    val icon = packageManager.getApplicationIcon(packageInfo.applicationInfo)
    return AppInfo(apkPath, packageName, versionName, versionCode.toLong(), appName, icon)
}

fun Context.getAppInfos(apkFolderPath: String): List<AppInfo> {
    val appInfoList = ArrayList<AppInfo>()
    for (file in File(apkFolderPath).listFiles())
        appInfoList.add(getAppInfo(file.path))
    return appInfoList
}

