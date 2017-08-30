package org.tianjyan.blurbg.BlurHelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.View;

import org.tianjyan.blurbg.BlurProcess.IBlurProcess;

public class BlurBehind {
    private static final String KEY_CACHE_BLURRED_BACKGROUND_IMAGE = "KEY_CACHE_BLURRED_BACKGROUND_IMAGE";
    private static final int CONSTANT_DEFAULT_ALPHA = 100;

    private static final LruCache<String, Bitmap> mImageCache = new LruCache<>(1);
    private static CacheBlurBehindAndExecuteTask cacheBlurBehindAndExecuteTask;

    private int mAlpha = CONSTANT_DEFAULT_ALPHA;
    private int mFilterColor = -1;
    private int mRadius = 1;
    private long captureBgDuration;
    private long blurBgDuration;

    private enum State {
        READY,
        EXECUTING
    }

    private State mState = State.READY;

    private static BlurBehind mInstance;

    public static BlurBehind getInstance() {
        if (mInstance == null) {
            mInstance = new BlurBehind();
        }
        return mInstance;
    }

    public void execute(Activity activity,
                        IBlurProcess iBlurProcess,
                        int radius,
                        OnBlurCompleteListener onBlurCompleteListener) {
        if (mState.equals(State.READY)) {
            mRadius = radius;
            mState = State.EXECUTING;
            cacheBlurBehindAndExecuteTask = new CacheBlurBehindAndExecuteTask(activity, iBlurProcess, onBlurCompleteListener);
            cacheBlurBehindAndExecuteTask.execute();
        }
    }

    public BlurBehind withAlpha(int alpha) {
        this.mAlpha = alpha;
        return this;
    }

    public BlurBehind withFilterColor(int filterColor) {
        this.mFilterColor = filterColor;
        return this;
    }

    public void setBackground(Activity activity) {
        if (mImageCache.size() != 0) {
            BitmapDrawable bd = new BitmapDrawable(activity.getResources(), mImageCache.get(KEY_CACHE_BLURRED_BACKGROUND_IMAGE));
            bd.setAlpha(mAlpha);
            if (mFilterColor != -1) {
                bd.setColorFilter(mFilterColor, PorterDuff.Mode.DST_ATOP);
            }
            activity.getWindow().setBackgroundDrawable(bd);
            mImageCache.remove(KEY_CACHE_BLURRED_BACKGROUND_IMAGE);
            cacheBlurBehindAndExecuteTask = null;
        }
    }

    private class CacheBlurBehindAndExecuteTask extends AsyncTask<Void, Void, Void> {
        private Activity activity;
        private OnBlurCompleteListener onBlurCompleteListener;
        private IBlurProcess blurProcess;

        private View decorView;
        private Bitmap image;

        public CacheBlurBehindAndExecuteTask(Activity activity, IBlurProcess blurProcess, OnBlurCompleteListener onBlurCompleteListener) {
            this.activity = activity;
            this.blurProcess = blurProcess;
            this.onBlurCompleteListener = onBlurCompleteListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            long startTime = System.currentTimeMillis();

            decorView = activity.getWindow().getDecorView();
            decorView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            decorView.setDrawingCacheEnabled(true);
            decorView.buildDrawingCache();

            image = decorView.getDrawingCache();

            captureBgDuration = System.currentTimeMillis() - startTime;
        }

        @Override
        protected Void doInBackground(Void... params) {
            long startTime = System.currentTimeMillis();

            Bitmap blurredBitmap = blurProcess.blur(image, mRadius);
            mImageCache.put(KEY_CACHE_BLURRED_BACKGROUND_IMAGE, blurredBitmap);

            blurBgDuration = System.currentTimeMillis() - startTime;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            decorView.destroyDrawingCache();
            decorView.setDrawingCacheEnabled(false);

            activity = null;

            String result = String.format("Capture Duration: %s ms\n" +
                            "Bitmap Size: %s kb\n" +
                            "Blur Duration: %s ms\n" +
                            "Blur Process: %s ",
                    captureBgDuration, image.getByteCount() / 1024, blurBgDuration, blurProcess.getClass().getSimpleName());

            onBlurCompleteListener.onBlurComplete(result);

            mState = State.READY;
        }
    }
}