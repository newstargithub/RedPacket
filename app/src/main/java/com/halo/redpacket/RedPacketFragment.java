package com.halo.redpacket;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.halo.redpacket.bean.RedPacketResp;


/**
 * Description:
 */

public class RedPacketFragment extends Fragment implements SensorEventListener{

    private static final float SHAKE_ACCELEROMETER_VALUE = 17;//摇晃幅度
    private static final long UPDATE_INTERVAL_TIME = 50;
    private static final int SPEED_SHAESHOLD = 20;//调节灵敏度
    private static final int SHAKE_DEGREES = 20;
    private static final String TAG = RedPacketFragment.class.getSimpleName();
    private SensorManager mSensorManager;
    private Sensor mAcceleration;
    private long lastUpateTime;
    private float lastX, lastY, lastZ;
    private TextView tvRedPacket;
    private View ivShake;
    private View rlBanner;
    private boolean isRedPacketReceived;//已领取
    private boolean isOpening;//领取中，防止多次点击摇晃重复领取
    private RedPacketDialogFragment mRedPacketDialogFragment;
    private double mRedPacketResult = 94110.00;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_red_packet, container, false);
        initView(view);
        return view;
    }

    protected void initView(View view) {
        rlBanner = view.findViewById(R.id.rl_red_banner);
        rlBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRedPacketBanner();
            }
        });
        tvRedPacket = (TextView) view.findViewById(R.id.tv_receive_hb);
        ivShake = view.findViewById(R.id.iv_hb_shake);
        initAccelerometerSensor();
    }


    /**
     * 点击领红包
     */
    private void onClickRedPacketBanner() {
        if (isRedPacketReceived) {
            //已经领取，直接弹窗
            showRedPacketDialog(mRedPacketResult, false);
        } else {
            receiveRedPacket();
        }
    }

    /**
     * 领红包
     */
    private void receiveRedPacket() {
        if (!isOpening) {
            isOpening = true;
            tvRedPacket.setText("红包要来啦...");
            //动画领取
            startShakePhoneAnim();
            tvRedPacket.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onReceiveRedPacketSuccess();
                    isOpening = false;
                }
            }, 1000);
        }
    }

    private void onReceiveRedPacketSuccess() {
        stopShakePhoneAnim();
        tvRedPacket.setText("谢谢参与");
        //停止传感器监听
//        unregisterShakeListener();
        showRedPacketDialog(mRedPacketResult, !isRedPacketReceived);
//        isRedPacketReceived = true;
    }

    /**
     * 展示红包弹窗
     */
    private void showRedPacketDialog(double result, boolean withAnimation) {
        RedPacketResp bean = new RedPacketResp();
        bean.setWithAnimation(withAnimation);
        bean.setRedPocketAmount(result);
        bean.setAccount("6666");
        mRedPacketDialogFragment = RedPacketDialogFragment.newInstance(bean);
        mRedPacketDialogFragment.show(getChildFragmentManager());
    }

    /**
     * 执行摇晃手机动画
     */
    private void startShakePhoneAnim() {
        RotateAnimation rotateAnimation = new RotateAnimation(SHAKE_DEGREES, -SHAKE_DEGREES, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatMode(Animation.REVERSE);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setDuration(300);
        ivShake.startAnimation(rotateAnimation);
    }

    private void stopShakePhoneAnim() {
        ivShake.clearAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerShakeListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterShakeListener();
    }

    /**
     * 初始化加速度传感器
     */
    private void initAccelerometerSensor() {
        Context context = getContext();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                mAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            } else {
                //设备上没有加速度传感器
                Log.w(TAG, "设备上没有加速度传感器");
            }
        }
    }

    /**
     * 开启传感器监听
     */
    private void registerShakeListener() {
        if (mSensorManager != null && mAcceleration != null) {
            mSensorManager.registerListener(this, mAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * 停止传感器监听，界面暂停或使用传感器后
     */
    private void unregisterShakeListener() {
        if (mSensorManager != null && mAcceleration != null) {
            mSensorManager.unregisterListener(this, mAcceleration);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //传感器值发生改变，加速度传感器返回3个值
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            //摇一摇算法加强
            long currentTimeMillis = System.currentTimeMillis();
            long timeInterval = currentTimeMillis - lastUpateTime;
            if (timeInterval < UPDATE_INTERVAL_TIME) {
                return;
            }
            lastUpateTime = currentTimeMillis;
            float aux = event.values[0];
            float auy = event.values[1];
            float auz = event.values[2];
            float deltaX = aux - lastX;
            float deltaY = auy - lastY;
            float deltaZ = auz - lastZ;
//            Log.d(TAG, "aux=" + aux + " auy=" + auy + " auz=" + auz);
            lastX = aux;
            lastY = auy;
            lastZ = auz;
            double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (speed >= SPEED_SHAESHOLD) {
                onShake();
            }
        }
        /*//获取传感器类型
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            float aux = event.values[0];
            float auy = event.values[1];
            float auz = event.values[2];
            LogUtil.d("aux=" + aux + " auy=" + auy + " auz=" + auz);
            if (Math.abs(aux) > SHAKE_ACCELEROMETER_VALUE || Math.abs(auy) > SHAKE_ACCELEROMETER_VALUE || Math.abs(auz) > SHAKE_ACCELEROMETER_VALUE) {
                onShake();
            }
        }*/
    }

    /**
     * 摇一摇
     */
    private void onShake() {
        if (isOpening || (mRedPacketDialogFragment != null && mRedPacketDialogFragment.getDialog() != null && mRedPacketDialogFragment.getDialog().isShowing())) {
            return;
        }
//        Log.d(TAG, "摇一摇");
        //播放摇一摇音乐
        RedpacketHelper.playShakeSound(getContext());
        //震动
        RedpacketHelper.vibrate(getContext(), 300);
        onClickRedPacketBanner();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //传感器的精度发生改变
    }


}
