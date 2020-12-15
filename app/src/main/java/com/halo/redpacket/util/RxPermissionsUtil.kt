package com.halo.redpacket.util

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.halo.redpacket.R
import com.halo.redpacket.extend.toast
import com.tbruyelle.rxpermissions2.RxPermissions

class RxPermissionsUtil {

    companion object {
        /**
         * 请求版本升级权限
         */
        fun requestUpgradePermission(activity: AppCompatActivity, versionUpgrade: ()-> Unit, askForPermissionRead: ()->Unit) {
            support(Build.VERSION_CODES.M, { // android 6.0 之后的操作
                RxPermissions(activity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.REQUEST_INSTALL_PACKAGES)
                        .subscribe {
                            if (it) {
                                versionUpgrade()
                            } else {
                                askForPermissionRead()
                            }
                        }
            }, { // android 6.0 之前的操作
                RxPermissions(activity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe {
                            if (it) {
                                versionUpgrade()
                            } else {
                                toast(R.string.write_external_storage_permission_tips).show()
                            }
                        }
            })
        }
    }
}