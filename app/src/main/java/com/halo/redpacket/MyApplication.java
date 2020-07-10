package com.halo.redpacket;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication sInstance;

    public static MyApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
