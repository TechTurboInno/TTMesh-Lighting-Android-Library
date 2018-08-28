package com.techturbo.bluetooth.light.widget;

import android.content.Context;
import android.graphics.Matrix;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.techturbo.ttmeslight.R;

import java.lang.ref.SoftReference;

public class LoadingView extends android.support.v7.widget.AppCompatImageView {
    private MyRunable runnable;
    private int width;
    private int height;
    private Drawable drawable;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
        runnable.startload();
    }

    /**
     * 设置静态的图片
     *
     * @param res
     */
    public void setDrawable(int res) {
        runnable.stopload();
        setImageDrawable(getResources().getDrawable(res));
    }

    public void startLoading() {

        if (runnable == null) {
            init();
            runnable.startload();
        }

        setImageDrawable(drawable);
        runnable.startload();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        runnable.stopload();

    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
        drawable = getDrawable();
        if (drawable == null) {
            drawable = getResources().getDrawable(R.drawable.loading1);
            setImageDrawable(drawable);
        }
        measure(0, 0);
        width = getMeasuredWidth() / 2;
        height = getMeasuredHeight() / 2;
        runnable = new MyRunable(this);
    }


    static class MyRunable implements Runnable {
        private boolean flag;
        private SoftReference<LoadingView> loadingViewSoftReference;
        private float degrees = 0f;
        private Matrix max;

        public MyRunable(LoadingView loadingView) {
            loadingViewSoftReference = new SoftReference<LoadingView>(loadingView);
            max = new Matrix();
        }

        @Override
        public void run() {
            if (loadingViewSoftReference.get().runnable != null && max != null) {
                degrees += 30f;
                max.setRotate(degrees, loadingViewSoftReference.get().width, loadingViewSoftReference.get().height);
                loadingViewSoftReference.get().setImageMatrix(max);
                if (degrees == 360) {
                    degrees = 0;
                }
                if (flag) {
                    loadingViewSoftReference.get().postDelayed(loadingViewSoftReference.get().runnable, 160);
                } else {
                    max.setRotate(0, loadingViewSoftReference.get().width, loadingViewSoftReference.get().height);
                    loadingViewSoftReference.get().setImageMatrix(max);
                }
            }
        }

        public void stopload() {
            flag = false;

        }

        public void startload() {
            flag = true;
            if (loadingViewSoftReference.get().runnable != null && max != null) {
                loadingViewSoftReference.get().postDelayed(loadingViewSoftReference.get().runnable, 80);
            }
        }
    }
}

