package org.tianjyan.blurbg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.tianjyan.blurbg.BlurProcess.BlurOperation;
import org.tianjyan.blurbg.BlurProcess.BlurOperationCache;
import org.tianjyan.blurbg.BlurProcess.IBlurProcess;
import org.tianjyan.blurbg.BlurProcess.JNIBlurProcess;
import org.tianjyan.blurbg.BlurProcess.JavaBlurProcess;
import org.tianjyan.blurbg.BlurProcess.RSBlurProcess;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public final static String PERFORMANCE = "performance";
    private int mRadius = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button javaBlurBtn = (Button) findViewById(R.id.javaBlurBtn);
        Button jniBlurBtn = (Button) findViewById(R.id.jniBlurBtn);
        Button rsBlurBtn = (Button) findViewById(R.id.rsBlurBtn);
        Button testBtn = (Button) findViewById(R.id.testBtn);

        TextView radiusValueTV = (TextView) findViewById(R.id.radiusValueTV);
        SeekBar radiusSB = (SeekBar) findViewById(R.id.radiusSB);

        IBlurProcess javaBlurProcess = new JavaBlurProcess();
        IBlurProcess jniBlurProcess = new JNIBlurProcess();
        IBlurProcess rsBlurProcess = new RSBlurProcess(this);

        javaBlurBtn.setOnClickListener(view -> {
            blur(javaBlurProcess, mRadius * 5);
        });
        jniBlurBtn.setOnClickListener(view -> {
            blur(jniBlurProcess, mRadius * 5);
        });
        rsBlurBtn.setOnClickListener(view -> {
            blur(rsBlurProcess, mRadius);
        });
        testBtn.setOnClickListener(view -> {
            testBtn.setEnabled(false);
            javaBlurBtn.setEnabled(false);
            jniBlurBtn.setEnabled(false);
            rsBlurBtn.setEnabled(false);
            testBtn.setText(getString(R.string.gathering));
            radiusSB.setEnabled(false);
            Observable.just(this)
                    .map(activity -> {
                        Log.d("ytj", Thread.currentThread().getName());
                        ArrayList<Integer> radiusList = new ArrayList<>();
                        radiusList.add(10);
                        radiusList.add(15);
                        radiusList.add(20);
                        radiusList.add(25);
                        radiusList.add(35);
                        radiusList.add(60);
                        radiusList.add(80);
                        for (Integer radius : radiusList) {
                            ArrayList<IBlurProcess> processes = new ArrayList<>();
                            processes.add(javaBlurProcess);
                            processes.add(jniBlurProcess);
                            if (radius <= 25) {
                                processes.add(rsBlurProcess);
                            }
                            for (IBlurProcess process : processes) {
                                for (int i=0; i < 20; i++) {
                                    BlurOperationCache.removeBlurredBitmap();
                                    BlurOperation blurOperation = new BlurOperation(process, radius);
                                    blurOperation.blur(MainActivity.this);
                                }
                            }
                        }
                        return "OK";
                    }).subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        Log.d("ytj", Thread.currentThread().getName());
                        testBtn.setEnabled(true);
                        javaBlurBtn.setEnabled(true);
                        jniBlurBtn.setEnabled(true);
                        rsBlurBtn.setEnabled(true);
                        testBtn.setText(getString(R.string.test_mode));
                        radiusSB.setEnabled(true);
                    });
        });

        radiusSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radiusValueTV.setText(String.valueOf(i));
                mRadius = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void blur(IBlurProcess blurProcess, int mRadius) {
        BlurOperation blurOperation = new BlurOperation(blurProcess, mRadius);
        Observable.just(this)
                .map(activity -> {
                    Log.d("ytj", Thread.currentThread().getName());
                    return blurOperation.blur(activity);
                }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    Log.d("ytj", Thread.currentThread().getName());
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    intent.putExtra(PERFORMANCE, result);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                });
    }
}
