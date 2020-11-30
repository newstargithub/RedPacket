package com.halo.redpacket.jnicall;

/**
 * Author: zx
 * Date: 2020/10/25
 * Description:
 * 生成头文件
 * D:\Android\WorkSpace\github\RedPacket\app\src\main\java>javah -classpath . -jni
 * com.halo.redpacket.jnicall.Native
 */
public class Native {
    public native void initilize();
    public native void threadStart();
    public native void threadStop();

    public void onNativeCallback(int count) {
//        Log.d("Native", "onNativeCallback count=" + count);
    }

    static {
        System.loadLibrary("native");
    }
}
