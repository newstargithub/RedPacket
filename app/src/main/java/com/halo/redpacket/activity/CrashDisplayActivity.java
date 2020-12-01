package com.halo.redpacket.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.halo.redpacket.R;

/**
 * 展示所捕获的异常信息
 */
public class CrashDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_display);
    }


}
