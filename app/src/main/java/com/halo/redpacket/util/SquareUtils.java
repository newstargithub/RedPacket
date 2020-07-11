package com.halo.redpacket.util;

import android.content.Context;

import com.halo.redpacket.MyApplication;
import com.halo.redpacket.okhttp.ProgressListener;
import com.halo.redpacket.okhttp.ProgressResponseBody;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class SquareUtils {

    private static OkHttpClient client;

    /**
     * 设置OkHttpClient通用设置，如timeout时间，DNS，Log日志，interceptor
     * @return
     */
    public static synchronized OkHttpClient getClient() {
        File cacheDir = MyApplication.getInstance().getExternalCacheDir();
        if (client == null) {
            client = new OkHttpClient.Builder()
                    //Interceptor -> cache -> NetworkInterceptor
                    //.addNetworkInterceptor(getLogger())
                    .cache(new Cache(new File(cacheDir, "okhttp"), 60 * 1024 * 1024))
                    //.dispatcher(getDispatcher())
                    //.dns(HTTP_DNS)
                    .build();
        }
        return client;
    }

    /**
     * 通过添加一个拦截器，向上层通知请求进度的结果
     * @param listener ProgressListener
     * @return
     */
    private static OkHttpClient getProgressBarClient(final ProgressListener listener) {
        return getClient().newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), listener))
                        .build();
            }
        }).build();
    }

    /**
     * 获取一个自带进度的Picasso
     * @param context
     * @param listener 进度回调
     * @return
     */
    public static Picasso getPicasso(Context context, ProgressListener listener) {
        OkHttpClient client = getProgressBarClient(listener);
        OkHttp3Downloader downloader = new OkHttp3Downloader(client);
        return new Picasso.Builder(context).downloader(downloader)
                .memoryCache(com.squareup.picasso.Cache.NONE)
                .build();
    }
}
