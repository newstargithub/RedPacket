package com.halo.redpacket.skin;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.halo.redpacket.R;

public class SkinActivity extends AppCompatActivity {

    private SkinFactory skinFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().setContext(getApplicationContext());
        skinFactory = new SkinFactory();
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), skinFactory);
        setContentView(R.layout.activity_skin);
        findViewById(R.id.bt_skin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skinApply();
            }
        });
    }

    /**
     * 一键换肤
     */
    private void skinApply() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/skin.apk";
        SkinManager.getInstance().loadSkinApk(path);
        skinFactory.apply();
    }
}
