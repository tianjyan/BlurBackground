package org.tianjyan.blurbg.BlurProcess;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BlurOperation {
    private IBlurProcess blurProcess;
    private int radius;

    public BlurOperation(IBlurProcess blurProcess, int radius) {
        if (blurProcess == null) {
            throw new IllegalArgumentException("BlurProcess can not be null.");
        }

        if (radius < 0) {
            throw new IllegalArgumentException("Radius should be positive.");
        }

        this.blurProcess = blurProcess;
        this.radius = radius;
    }

    public String blur(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity can not be null.");
        }

        // Capture Bitmap
        long startTime = System.currentTimeMillis();
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap captureBitmap = decorView.getDrawingCache();
        int bitmapSize = captureBitmap.getByteCount() / 1024;
        long captureBackgroundDuration = System.currentTimeMillis() - startTime;

        // Blur Bitmap
        startTime = System.currentTimeMillis();
        Bitmap blurredBitmap = blurProcess.blur(captureBitmap, radius);
        long blurBackgroundDuration = System.currentTimeMillis() - startTime;
        BlurOperationCache.putBlurredBitmap(blurredBitmap);

        // Save File
        File file = new File(getCacheDir(activity.getApplicationContext()), getSaveFileName());
        try {
            boolean exists = file.exists();
            FileWriter fileWriter = new FileWriter(file, true);
            if (!exists) {
                fileWriter.write(String.format("CaptureBg Duration, Bitmap Size, Blur Duration, Radius\n"));
            }
            fileWriter.write(String.format("%s ms, %s kb, %s ms, %s\n", captureBackgroundDuration, bitmapSize, blurBackgroundDuration, radius));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Clean up
        decorView.destroyDrawingCache();
        decorView.setDrawingCacheEnabled(false);

        // Generate Result
        String result = String.format("Capture Duration: %s ms\n" +
                        "Bitmap Size: %s kb\n" +
                        "Blur Duration: %s ms\n" +
                        "Blur Process: %s ",
                captureBackgroundDuration, bitmapSize, blurBackgroundDuration, blurProcess.getClass().getSimpleName());

        return result;
    }

    private String getSaveFileName() {
        return String.format("%s-%s-%s-%s.csv", Build.MODEL, radius, Build.VERSION.SDK_INT, blurProcess.getClass().getSimpleName());
    }

    private File getCacheDir(Context context) {
        File cacheDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && context.getExternalCacheDir() != null)
            cacheDir = context.getExternalCacheDir();
        else {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }
}
