package com.halo.redpacket.mvp;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

/**
 * Author: zx
 * Date: 2020/7/9
 * Description:
 */
public class BasePresenter implements IPresenter {
    private static final String TAG = BasePresenter.class.getSimpleName();

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {

    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        //在 onDestroy 方法中停止正在执行的耗时操作
    }
}
