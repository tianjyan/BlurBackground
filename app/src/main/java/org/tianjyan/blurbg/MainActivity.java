package org.tianjyan.blurbg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.tianjyan.blurbg.BlurHelper.BlurBehind;
import org.tianjyan.blurbg.BlurProcess.IBlurProcess;
import org.tianjyan.blurbg.BlurProcess.JNIBlurProcess;
import org.tianjyan.blurbg.BlurProcess.JavaBlurProcess;
import org.tianjyan.blurbg.BlurProcess.RSBlurProcess;

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
        TextView radiusValueTV = (TextView) findViewById(R.id.radiusValueTV);
        SeekBar radiusSB = (SeekBar) findViewById(R.id.radiusSB);

        IBlurProcess javaBlurProcess = new JavaBlurProcess();
        IBlurProcess jniBlurProcess = new JNIBlurProcess();
        IBlurProcess rsBlurProcess = new RSBlurProcess(this);

        javaBlurBtn.setOnClickListener(view -> {
            blur(javaBlurProcess, mRadius);
        });
        jniBlurBtn.setOnClickListener(view -> {
            blur(jniBlurProcess, mRadius);
        });
        rsBlurBtn.setOnClickListener(view -> {
            blur(rsBlurProcess, mRadius);
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
        BlurBehind.getInstance().execute(this , blurProcess, mRadius, (result) -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra(PERFORMANCE, result);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
    }
}
