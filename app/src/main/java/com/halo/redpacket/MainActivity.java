package com.halo.redpacket;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.halo.redpacket.mvp.BasePresenter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BasePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RedPacketFragment fragment = new RedPacketFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_container, fragment).commit();

        getLifecycle().addObserver(new GenericLifecycleObserver() {

            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                Log.d(TAG, "onStateChanged: event =" + event);
            }
        });

        mPresenter = new BasePresenter();
        getLifecycle().addObserver(mPresenter);
    }
}
