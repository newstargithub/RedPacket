package com.halo.redpacket.skin;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.DisplayMetrics;

import java.lang.reflect.Method;

/**
 * Author: zx
 * Date: 2020/10/27
 * Description: 单例皮肤管理器
 * 1.下载皮肤包（apk,lib）到本地
 * 2.动态加载资源包
 * 3.获取所有需要换肤的控件的bg,textColor,src属性
 * 4.需要换肤的控件的资源id中去匹配皮肤的资源，进行替换
 */
public class SkinManager {


    private static SkinManager sInstance = new SkinManager();
    private Context context;
    private String skinPackageName;
    private Resources resources;

    public static SkinManager getInstance() {
        return sInstance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 加载皮肤资源
     * @param path
     */
    public void loadSkinApk(String path) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        skinPackageName = packageArchiveInfo.packageName;
        try {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            Configuration config = context.getResources().getConfiguration();
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assetManager, path);
            resources = new Resources(assetManager, metrics, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 去皮肤资源包中获取颜色
     * @param resId 当前应用的资源id
     * @return
     */
    public int getColor(@ColorRes int resId) {
        if (resources == null) {
            return ContextCompat.getColor(context, resId);
        }
        //获取资源id的类型
        String resourceTypeName = context.getResources().getResourceTypeName(resId);
        //获取资源id的名字
        String resourceEntryName = context.getResources().getResourceEntryName(resId);
        //资源包对应的id
        int identifier = resources.getIdentifier(resourceEntryName, resourceTypeName, skinPackageName);
        if (identifier == 0) {
            return ContextCompat.getColor(context, resId);
        }
        return resources.getColor(identifier);
    }

    /**
     * 去皮肤资源包中获取图片
     * @param resId
     * @return
     */
    public Drawable getDrawable(@DrawableRes int resId) {
        if (resources == null) {
            return ContextCompat.getDrawable(context, resId);
        }
        //获取资源id的类型
        String resourceTypeName = context.getResources().getResourceTypeName(resId);
        //获取资源id的名字
        String resourceEntryName = context.getResources().getResourceEntryName(resId);
        //资源包对应的id
        int identifier = resources.getIdentifier(resourceEntryName, resourceTypeName, skinPackageName);
        if (identifier == 0) {
            return ContextCompat.getDrawable(context, resId);
        }
        return resources.getDrawable(identifier);
    }

    public void setLayout(Activity activity) {
        LayoutInflaterCompat.setFactory2(activity.getLayoutInflater(), new SkinFactory());
    }
}
