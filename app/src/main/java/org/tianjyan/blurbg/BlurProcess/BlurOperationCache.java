package org.tianjyan.blurbg.BlurProcess;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.LruCache;

public class BlurOperationCache {
    private static final LruCache<String, Bitmap> imageCache = new LruCache<>(1);
    private static final String KEY_CACHE_BLURRED_BACKGROUND_IMAGE = "KEY_CACHE_BLURRED_BACKGROUND_IMAGE";

    public static void setBackground(Activity activity) {
        if (imageCache.size() != 0) {
            BitmapDrawable bd = new BitmapDrawable(activity.getResources(), imageCache.get(KEY_CACHE_BLURRED_BACKGROUND_IMAGE));
            activity.getWindow().setBackgroundDrawable(bd);
            imageCache.remove(KEY_CACHE_BLURRED_BACKGROUND_IMAGE);
        }
    }

    public static void putBlurredBitmap(Bitmap bitmap) {
        imageCache.put(KEY_CACHE_BLURRED_BACKGROUND_IMAGE, bitmap);
    }

    public static void removeBlurredBitmap() {
        if (imageCache.size() != 0) {
            imageCache.remove(KEY_CACHE_BLURRED_BACKGROUND_IMAGE);
        }
    }
}
