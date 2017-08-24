package org.tianjyan.blurbg;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.tianjyan.blurbg.BlurHelper.BlurBehind;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        String result = getIntent().getStringExtra(MainActivity.PERFORMANCE);
        TextView perfTV = (TextView) findViewById(R.id.perfTV);
        perfTV.setText(result);

        BlurBehind.getInstance()
                .withAlpha(50)
                .withFilterColor(Color.parseColor("#0075c0"))
                .setBackground(this);
    }
}
