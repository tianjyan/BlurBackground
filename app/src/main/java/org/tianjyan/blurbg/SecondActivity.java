package org.tianjyan.blurbg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.tianjyan.blurbg.BlurProcess.BlurOperationCache;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        String result = getIntent().getStringExtra(MainActivity.PERFORMANCE);
        TextView perfTV = (TextView) findViewById(R.id.perfTV);
        perfTV.setText(result);

        BlurOperationCache.setBackground(this);
    }
}
