package com.halo.redpacket.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

/**
 * 权限申请工具类
 * Andorid10.0 系统增加了对外置存储访问的限制
 * Environment.getExternalStorageDirectory().getAbsolutePath()；没有权限获取不到
 * 临时解决方案是在清单文件中添加设置，requestLegacyExternalStorage="true"
 */
public class PermissionUtil {

    private static final int REQUEST_CONTACTS_CODE = 1;
    private static final String MY_SP = "Permission";

    public void requestContactPermission(Activity context, View view) {
        String contactsPermission = Manifest.permission.WRITE_CONTACTS;
        if (showAskPermission()) {
            //需要动态申请权限
            if (ContextCompat.checkSelfPermission(context, contactsPermission) != PackageManager.PERMISSION_GRANTED) {
                //用户之前的操作
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, contactsPermission)) {
                    //用户在对话框中拒绝权限，并没有选中不再询问
                    ActivityCompat.requestPermissions(context, new String[]{contactsPermission}, REQUEST_CONTACTS_CODE);
                } else {
                    if (isFirstTimeAsking(context, contactsPermission)) {
                        Toast.makeText(context, "第一次申请权限", Toast.LENGTH_SHORT).show();
                        firstTimeAsking(context, contactsPermission, false);
                        ActivityCompat.requestPermissions(context, new String[]{contactsPermission}, REQUEST_CONTACTS_CODE);
                    } else {
                        Toast.makeText(context, "权限之前被拒绝，并且选择不再提示", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                //权限已经申请过
                Toast.makeText(context, "权限已申请", Toast.LENGTH_SHORT).show();
            }
        } else {
            //不需要动态申请权限
        }
    }

    private static void firstTimeAsking(Activity context, String permission, boolean firstTime) {
        SharedPreferences sp = context.getSharedPreferences(MY_SP, Context.MODE_PRIVATE);
        sp.edit().putBoolean(permission, firstTime).apply();
    }

    private static boolean isFirstTimeAsking(Activity context, String permission) {
        SharedPreferences sp = context.getSharedPreferences(MY_SP, Context.MODE_PRIVATE);
        return sp.getBoolean(permission, false);
    }

    private static boolean showAskPermission() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.M;
    }

    public static void requestPermission(Activity context, String permission, PermissionRequestListener listener) {
        if (showAskPermission()) {
            //需要动态申请权限
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                //用户之前的操作
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                    //用户在对话框中拒绝权限，并没有选中不再询问
                    listener.onPermissionPreviouslyDenied();
                } else {
                    if (isFirstTimeAsking(context, permission)) {
                        firstTimeAsking(context, permission, false);
                        listener.onFirstRequestPermission();
                    } else {
                        listener.onPermissionPreviouslyDeniedWithNeverAsk();
                    }
                }
            } else {
                //权限已经申请过
                listener.onPermissionGranted();
            }
        } else {
            //不需要动态申请权限
            listener.onPermissionGranted();
        }
    }

    public interface PermissionRequestListener {
        /**
         * 第一次申请权限
         */
        void onFirstRequestPermission();

        /**
         * 拒绝权限
         */
        void onPermissionPreviouslyDenied();

        /**
         * 拒绝权限，并选中不再提示
         */
        void onPermissionPreviouslyDeniedWithNeverAsk();

        /**
         * 权限以获取
         */
        void onPermissionGranted();
    }
}
