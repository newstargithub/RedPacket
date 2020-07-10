package com.halo.redpacket.okhttp;

import android.os.RecoverySystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.ForwardingSource;
import okio.Okio;
import okio.Options;
import okio.Sink;
import okio.Source;
import okio.Timeout;

/**
 * 继承ResponseBody获取网络请求的进度
 */
public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(BufferedSource source) {
        return new ForwardingSource(source) {
            long totalByteRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long byteRead = super.read(sink, byteCount);
                totalByteRead += byteRead != -1 ? byteRead : 0;
                if (progressListener != null) {
                    progressListener.onProgress((int) (100 * totalByteRead / responseBody.contentLength()));
                }
                return byteRead;
            }
        };
    }

}
