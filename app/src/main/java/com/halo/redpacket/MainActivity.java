package com.halo.redpacket;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.halo.redpacket.model.RetrofitManager;
import com.halo.redpacket.model.RetryWithDelay;
import com.halo.redpacket.mvp.BasePresenter;
import com.halo.redpacket.util.RxJavaUtils;
import com.safframework.lifecycle.RxLifecycle;

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


        mPresenter = new BasePresenter();
        getLifecycle().addObserver(mPresenter);

        getPublishEvent();
    }

    /**
     * 获取在 github 上的公共事件。
     */
    private void getPublishEvent() {
        RetrofitManager.get()
                .apiService()
                .publishEvent("fengzhizi715")
                //重试机制
                .retryWhen(new RetryWithDelay(3,1000))
                //RxLifecycle 可以防止内存泄漏，当然除了使用它之外还有多种方式。
                .compose(RxLifecycle.bind(this).toLifecycleTransformer())
                .compose(RxJavaUtils.maybeToMain())
                .subscribe();
    }
}
