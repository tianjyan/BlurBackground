package org.tianjyan.blurbg;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.tianjyan.blurbg.BlurProcess.IBlurProcess;
import org.tianjyan.blurbg.BlurProcess.RSBlurProcess;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WallpaperBgActivity extends AppCompatActivity {
    TextView perfTV;
    TextView radiusValueTV;
    SeekBar radiusSB;
    Button blurBtn;

    int radius = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_bg);
        perfTV = (TextView) findViewById(R.id.perfTV);
        radiusValueTV = (TextView) findViewById(R.id.radiusValueTV);
        radiusSB = (SeekBar) findViewById(R.id.radiusSB);
        blurBtn = (Button) findViewById(R.id.blurBtn);
        RSBlurProcess rsBlurProcess = new RSBlurProcess(this);

        radiusSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radiusValueTV.setText(String.valueOf(i));
                radius = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        blurBtn.setOnClickListener(v -> {
          blur(rsBlurProcess, radius);
        });
        blur(rsBlurProcess, radius);
    }

    public void blur(IBlurProcess blurProcess, int mRadius) {
        blurBtn.setEnabled(false);
        Observable.just(this)
                .map(activity -> {
                    Log.d("ytj", Thread.currentThread().getName());
                    // Get Current Wallpaper
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                    Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) wallpaperDrawable).getBitmap();
                    int bitmapSize = bitmap.getByteCount() / 1024;

                    // Blur Bitmap
                    long startTime = System.currentTimeMillis();
                    Bitmap blurredBitmap = blurProcess.blur(bitmap, mRadius);
                    blurredBitmap = blurProcess.blur(blurredBitmap, mRadius);
                    blurredBitmap = blurProcess.blur(blurredBitmap, mRadius);
                    long blurBackgroundDuration = System.currentTimeMillis() - startTime;

                    // Generate Result
                    String result = String.format("Bitmap Size: %s kb\n" +
                                    "Blur Duration: %s ms\n" +
                                    "Blur Process: %s ",
                            bitmapSize, blurBackgroundDuration, blurProcess.getClass().getSimpleName());

                    return new BlurredResult(blurredBitmap, result);
                }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(blurredResult -> {
                    Log.d("ytj", Thread.currentThread().getName());
                    BitmapDrawable bd = new BitmapDrawable(this.getResources(),blurredResult.getBitmap());
                    this.getWindow().setBackgroundDrawable(bd);
                    this.perfTV.setText(blurredResult.getResult());
                    blurBtn.setEnabled(true);
                });
    }

    private class BlurredResult {
        private final Bitmap bitmap;
        private final String result;
        BlurredResult(Bitmap bitmap, String result) {
            this.bitmap = bitmap;
            this.result = result;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public String getResult() {
            return result;
        }
    }
}
