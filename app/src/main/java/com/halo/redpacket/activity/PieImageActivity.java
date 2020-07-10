package com.halo.redpacket.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.halo.redpacket.R;
import com.halo.redpacket.okhttp.ProgressListener;
import com.halo.redpacket.util.SquareUtils;
import com.halo.redpacket.widget.PieImageView;
import com.squareup.picasso.Picasso;

public class PieImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_image);

        final PieImageView pieImageView = (PieImageView) findViewById(R.id.pieImageView);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        ProgressListener listener = new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                pieImageView.setProgress(progress);
            }
        };
        Picasso picasso = SquareUtils.getPicasso(this, listener);
        picasso.load("https://m2.cmbwlbszit.com:8443/WebStaticResouces/static/images/MB0003/HomePage/NewPreferetial/ksdc_HK.png")
                .placeholder(pieImageView.getDrawable())
                .config(Bitmap.Config.ARGB_4444)
                .into(pieImageView);

        Picasso.get().load("https://m2.cmbwlbszit.com:8443/WebStaticResouces/static/images/MB0003/HomePage/NewPreferetial/ksdc_HK.png").into(imageView);
    }
}
