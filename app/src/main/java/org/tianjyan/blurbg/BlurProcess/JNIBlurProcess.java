package org.tianjyan.blurbg.BlurProcess;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JNIBlurProcess implements IBlurProcess  {
    private static native void functionToBlur(Bitmap bitmapOut, int radius, int threadCount, int threadIndex, int round);

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public Bitmap blur(Bitmap original, float radius) {
        Bitmap bitmapOut = original.copy(Bitmap.Config.ARGB_8888, true);

        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(cores);

        ArrayList<NativeTask> horizontal = new ArrayList<>(cores);
        ArrayList<NativeTask> vertical = new ArrayList<>(cores);
        for (int i = 0; i < cores; i++) {
            horizontal.add(new NativeTask(bitmapOut, (int) radius, cores, i, 1));
            vertical.add(new NativeTask(bitmapOut, (int) radius, cores, i, 2));
        }

        try {
            executorService.invokeAll(horizontal);
        } catch (InterruptedException e) {
            return bitmapOut;
        }

        try {
            executorService.invokeAll(vertical);
        } catch (InterruptedException e) {
            return bitmapOut;
        }
        return bitmapOut;
    }

    private static class NativeTask implements Callable<Void> {
        private final Bitmap _bitmapOut;
        private final int _radius;
        private final int _totalCores;
        private final int _coreIndex;
        private final int _round;

        public NativeTask(Bitmap bitmapOut, int radius, int totalCores, int coreIndex, int round) {
            _bitmapOut = bitmapOut;
            _radius = radius;
            _totalCores = totalCores;
            _coreIndex = coreIndex;
            _round = round;
        }

        @Override
        public Void call() throws Exception {
            Log.i("JNIBlurProcess", "StartBluring");
            functionToBlur(_bitmapOut, _radius, _totalCores, _coreIndex, _round);
            Log.i("JNIBlurProcess", "EndBluring");
            return null;
        }

    }
}
