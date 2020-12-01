package com.halo.redpacket.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PieImageView extends androidx.appcompat.widget.AppCompatImageView {

    private static final int MAX_PROGRESS = 100;
    private int mProgress;
    private Paint mArcPaint;
    private Paint mCirclePaint;
    private RectF mBound;

    public PieImageView(@NonNull Context context) {
        this(context, null, 0);
    }

    public PieImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    private void init(Context context, AttributeSet attrs) {
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mArcPaint.setStrokeWidth(dpToPixel(0.1f, getContext()));
        mArcPaint.setColor(Color.RED);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(dpToPixel(2, getContext()));
        mCirclePaint.setColor(Color.argb(120, 0xff, 0xff, 0xff));
        mBound = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mProgress != MAX_PROGRESS && mProgress != 0) {
            float angle = mProgress * 360f / MAX_PROGRESS;
            canvas.drawArc(mBound, 270, angle, true, mArcPaint);
            canvas.drawCircle(mBound.centerX(), mBound.centerY(), mBound.height() / 2, mCirclePaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        // 判断是wrap_content的测量模式
        if (widthSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.AT_MOST) {
            int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
            int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
            // 将宽高设置为传入宽高的最小值
            int size = Math.min(measuredWidth, measuredHeight);
            setMeasuredDimension(size, size);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int min = Math.min(w, h);
        int max = w + h - min;
        int r = Math.min(w, h) / 3;
        mBound.set((max >> 1) - r, (min >> 1) - r, (max >> 1) + r, (min >> 1) + r);
    }

    private float scale = 0;

    private int dpToPixel(float dp, Context context) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dp * scale);
    }

    public void setProgress(@IntRange(from = 0, to = MAX_PROGRESS) int progress) {
        this.mProgress = progress;
        ViewCompat.postInvalidateOnAnimation(this);
    }

}
