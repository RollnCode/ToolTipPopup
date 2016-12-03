package com.rollncode.tooltippopuptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.rollncode.tooltippopup.TooltipPopup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TooltipPopup.Builder(view, R.drawable.tooltip_dots)
                        .setAboveAnchor(false)
                        .setGravity(Gravity.START)
                        .show();
            }
        });
    }
}
