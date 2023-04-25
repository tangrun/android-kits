package com.tangrun.kits.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.tangrun.kits.R;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/08/23
 * desc   : 按照比例显示的 FrameLayout
 */
public final class RatioFrameLayout extends FrameLayout {

    /**
     * 宽高比例
     */
    private float mWidthRatio;
    private float mHeightRatio;

    public RatioFrameLayout(Context context) {
        this(context, null);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RatioFrameLayout);
        String sizeRatio = array.getString(R.styleable.RatioFrameLayout_sizeRatio);
        if (!TextUtils.isEmpty(sizeRatio)) {
            String[] arrays = sizeRatio.split(":");
            if (arrays.length == 2) {
                mWidthRatio = Float.parseFloat(arrays[0]);
                mHeightRatio = Float.parseFloat(arrays[1]);
            } else {
                throw new IllegalArgumentException("are you ok?");
            }
        }
        array.recycle();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d("TAG", "onLayout: " + this);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidthRatio != 0 && mHeightRatio != 0) {

            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

//            {
//                Log.d("TAG", "onMeasure: " + this + " " + widthSpecMode + " " + widthSpecSize + " " + heightSpecMode + " " + heightSpecSize);
//            }

            boolean useOriginWidth = false, useOriginHeight = false;
            if (widthSpecMode == MeasureSpec.EXACTLY || heightSpecMode == MeasureSpec.EXACTLY) {
                if (widthSpecMode == heightSpecMode) {
                    if (widthSpecSize <= heightSpecSize){
                        useOriginWidth = true;
                    }else {
                        useOriginHeight = true;
                    }
                } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                    useOriginWidth = true;
                } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                    useOriginHeight = true;
                }
            } else if (widthSpecMode == MeasureSpec.AT_MOST && widthSpecSize > 0) {
                useOriginWidth = true;
            } else if (heightSpecMode == MeasureSpec.AT_MOST && heightSpecSize > 0) {
                useOriginHeight = true;
            }

            if (useOriginWidth) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSpecSize / getSizeRatio()), MeasureSpec.EXACTLY);
            } else if (useOriginHeight) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.EXACTLY);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (heightSpecSize * getSizeRatio()), MeasureSpec.EXACTLY);
            }

            // 一般情况下 LayoutParams.WRAP_CONTENT 对应着 MeasureSpec.AT_MOST（自适应），但是由于我们在代码中强制修改了测量模式为 MeasureSpec.EXACTLY（固定值）
            // 这样会有可能重新触发一次 onMeasure 方法，这个时候传入测量模式的就不是 MeasureSpec.AT_MOST（自适应） 模式，而是 MeasureSpec.EXACTLY（固定值）模式
            // 所以我们要进行双重判断，首先判断 LayoutParams，再判断测量模式，这样就能避免因为修改了测量模式触发对宽高的重新计算，最终导致计算结果和上次计算的不同
//            if (layoutParams.width != LayoutParams.WRAP_CONTENT && layoutParams.height != LayoutParams.WRAP_CONTENT
//                    && widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
//                // 如果当前宽度和高度都是写死的
//                if (widthSpecSize / sizeRatio <= heightSpecSize) {
//                    // 如果宽度经过比例换算不超过原有的高度
//                    heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSpecSize / sizeRatio), MeasureSpec.EXACTLY);
//                } else if (heightSpecSize * sizeRatio <= widthSpecSize) {
//                    // 如果高度经过比例换算不超过原有的宽度
//                    widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (heightSpecSize * sizeRatio), MeasureSpec.EXACTLY);
//                }
//            } else if (layoutParams.width != LayoutParams.WRAP_CONTENT && widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode != MeasureSpec.EXACTLY) {
//                // 如果当前宽度是写死的，但是高度不写死
//                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSpecSize / sizeRatio), MeasureSpec.EXACTLY);
//            } else if (layoutParams.height != LayoutParams.WRAP_CONTENT && heightSpecMode == MeasureSpec.EXACTLY && widthSpecMode != MeasureSpec.EXACTLY) {
//                // 如果当前高度是写死的，但是宽度不写死
//                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (heightSpecSize * sizeRatio), MeasureSpec.EXACTLY);
//            }

//            {
//                widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//                widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
//
//                heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//                heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//                Log.d("TAG", "onMeasure-: " + this + " " + widthSpecMode + " " + widthSpecSize + " " + heightSpecMode + " " + heightSpecSize);
//            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        {
//            widthMeasureSpec = getMeasuredWidthAndState();
//            heightMeasureSpec = getMeasuredHeightAndState();
//            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
//
//            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//            Log.d("TAG", "onMeasure--: " + this + " " + widthSpecMode + " " + widthSpecSize + " " + heightSpecMode + " " + heightSpecSize);
//        }
    }


    public float getWidthRatio() {
        return mWidthRatio;
    }

    public float getHeightRatio() {
        return mHeightRatio;
    }

    /**
     * 获取宽高比
     */
    public float getSizeRatio() {
        return mWidthRatio / mHeightRatio;
    }

    /**
     * 设置宽高比
     */
    public void setSizeRatio(float widthRatio, float heightRatio) {
        mWidthRatio = widthRatio;
        mHeightRatio = heightRatio;
        requestLayout();
        invalidate();
    }
}