package org.tianjyan.blurbg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.tianjyan.blurbg.BlurHelper.BlurBehind;
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

        Button openActivityBtn = (Button) findViewById(R.id.openActivityBtn);
        TextView radiusValueTV = (TextView) findViewById(R.id.radiusValueTV);
        SeekBar radiusSB = (SeekBar) findViewById(R.id.radiusSB);

        openActivityBtn.setOnClickListener(view -> {
            BlurBehind.getInstance().execute(this , new JNIBlurProcess(), mRadius, (result) -> {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra(PERFORMANCE, result);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
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
}
