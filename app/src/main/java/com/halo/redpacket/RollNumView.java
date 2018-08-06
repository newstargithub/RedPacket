package com.halo.redpacket;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

/**
 * 数字随机滚动显示.
 */
public class RollNumView extends LinearLayout {
    private int mNumTextColor = Color.WHITE;
    private int mNumTextSize = 16;
    private double mNumberValue;
    private int mRollDuration = 1000;
    private int mRollRepeatCount = 1;
    private String mNumberFormat = "%,.2f";
    private Random mNumberRandom = new Random();
    private SparseIntArray mTextWidthArray = new SparseIntArray();
    private int mNumberCount;

    public RollNumView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RollNumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RollNumView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RollNumView, defStyle, 0);

        mNumTextColor = a.getColor(
                R.styleable.RollNumView_numTextColor,
                mNumTextColor);
        mNumTextSize = a.getDimensionPixelSize(
                R.styleable.RollNumView_numTextSize,
                mNumTextSize);
        mRollDuration = a.getInteger(
                R.styleable.RollNumView_rollDuration,
                mRollDuration);
        mRollRepeatCount = a.getInteger(
                R.styleable.RollNumView_rollRepeatCount,
                mRollRepeatCount);
        a.recycle();

        setOrientation(LinearLayout.HORIZONTAL);
        invalidateText();
    }

    private void invalidateText() {
        initRollNum();
    }

    /**
     * 文本颜色
     */
    public int getNumTextColor() {
        return mNumTextColor;
    }

    public void setNumTextColor(int exampleColor) {
        mNumTextColor = exampleColor;
        invalidateText();
    }

    /**
     * 文字大小
     */
    public int getNumTextSize() {
        return mNumTextSize;
    }

    public void setNumTextSize(int size) {
        mNumTextSize = size;
        invalidateText();
    }

    /**
     * 添加表示数值的文字
     */
    private void initRollNum() {
        removeAllViews();
        mTextWidthArray.clear();
        mNumberCount = 0;
        String numStr = String.format(Locale.getDefault(), mNumberFormat, mNumberValue);
        char[] chars = numStr.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            addText(c);
            if (isNumber(c)) {
                mNumberCount ++;
            }
        }
    }

    /**
     * 浮点数格式化字符串
     *
     * @param format
     */
    public void setNumberFormat(String format) {
        mNumberFormat = format;
    }

    /**
     * 是否数字字符
     *
     * @param c 字符
     * @return
     */
    private boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    private TextView addText(char c) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(mNumTextColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNumTextSize);
        textView.setText(String.valueOf(c));
        textView.setTag(c);
        int measuredWidth = mTextWidthArray.get(c);
        if (measuredWidth == 0) {
            int widthMeasureSpec = MeasureSpec.makeMeasureSpec((1 << 30) -1, MeasureSpec.AT_MOST);
            int heightMeasureSpec = MeasureSpec.makeMeasureSpec((1 << 30) -1, MeasureSpec.AT_MOST);
            textView.measure(widthMeasureSpec, heightMeasureSpec);
            measuredWidth = textView.getMeasuredWidth();
            mTextWidthArray.put(c, measuredWidth);
        }
        LayoutParams params = new LayoutParams(measuredWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(textView, params);
        return textView;
    }

    /**
     * 开始滚动
     */
    public void startRoll() {
        post(new Runnable() {
            @Override
            public void run() {
                rullToNum();
            }
        });
    }

    /**
     * 滚动数字
     */
    private void rullToNum() {
        int childCount = getChildCount();
        int numPos = 0;
        for (int i = 0; i < childCount; i++) {
            final TextView childView = (TextView) getChildAt(i);
            char tag = (char) childView.getTag();
            if (!isNumber(tag)) {
                continue;
            }
            numPos ++;
            setRandomNumber(childView);
            int height = childView.getHeight();
            final boolean isRollUp = isRollUpDirec(numPos);
            final float rollFrom = isRollUp ? -height : height;
            final float rollTo = isRollUp ? height : -height;
            float[] values = getAnimationValues(rollFrom, rollTo);
            ValueAnimator animator = ValueAnimator.ofFloat(values);
            animator.setDuration(mRollDuration);
            animator.setInterpolator(new DecelerateInterpolator());//减速变化
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                float animatedValueLast = 0;
                int time = 0;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    if ((isRollUp && animatedValueLast < 0 && animatedValue > 0)
                            || (!isRollUp && animatedValueLast > 0 && animatedValue < 0)) {
                        time ++;
                        boolean isLastTime = time + 1 >= mRollRepeatCount;
                        if (isLastTime) {
                            char tag = (char) childView.getTag();
                            childView.setText(String.valueOf(tag));
                        } else {
                            setRandomNumber(childView);
                        }
                    }
                    animatedValueLast = animatedValue;
                    childView.setTranslationY(animatedValue);
                }
            });
            animator.start();
        }
    }

    /**
     * 滚动方向向上
     * @param numPositon 数字位置(从左向右)
     * @return 向上
     */
    private boolean isRollUpDirec(int numPositon) {
        return Math.random() >= 0.5;
    }

    /**
     * 动画的变化值
     *
     * @param rollFrom 滚动起始点位移
     * @param rollTo   滚动结束点位移
     * @return
     */
    private float[] getAnimationValues(float rollFrom, float rollTo) {
        float[] values = new float[2 * mRollRepeatCount];
        for (int i = 0; i < values.length; i++) {
            boolean singular = i % 2 != 0;
            values[i] = singular ? rollFrom : rollTo;
        }
        values[values.length - 1] = 0;
        return values;
    }

    /**
     * 设置0-9随机数字
     *
     * @param textView 文本框
     */
    private void setRandomNumber(TextView textView) {
        int number = mNumberRandom.nextInt(10);
        textView.setText(String.valueOf(number));
    }

    /**
     * 设置最终显示数值
     *
     * @param numberValue 数值
     */
    public void setNumberValue(double numberValue) {
        mNumberValue = numberValue;
        initRollNum();
    }

    /**
     * 滚动次数，至少一次
     *
     * @param count
     */
    public void setRollRepeatCount(int count) {
        mRollRepeatCount = Math.max(count, 1);
    }

    /**
     * 滚动总时长
     *
     * @param duration
     */
    public void setRollDuration(int duration) {
        mRollDuration = duration;
    }

}
