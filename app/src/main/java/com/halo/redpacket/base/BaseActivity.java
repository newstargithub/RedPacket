package com.halo.redpacket.base;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.halo.redpacket.util.PermissionUtil;

public class BaseActivity extends AppCompatActivity {

    public void requestContactPermission(View view) {
        PermissionUtil.requestPermission(this, Manifest.permission.WRITE_CONTACTS, new PermissionUtil.PermissionRequestListener() {
            @Override
            public void onFirstRequestPermission() {

            }

            @Override
            public void onPermissionPreviouslyDenied() {

            }

            @Override
            public void onPermissionPreviouslyDeniedWithNeverAsk() {

            }

            @Override
            public void onPermissionGranted() {

            }
        });
    }
}
