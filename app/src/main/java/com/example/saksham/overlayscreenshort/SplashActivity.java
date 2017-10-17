package com.example.saksham.overlayscreenshort;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashActivity extends AppCompatActivity {

    ImageView ivLogo;
    LinearLayout llSplash;
    LinearLayout.LayoutParams params;
    int marginTop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        llSplash = (LinearLayout) findViewById(R.id.llSplash);

        params = new LinearLayout.LayoutParams(llSplash.getLayoutParams());

        CountDownTimer timer = new CountDownTimer(1000, 50) {
            @Override
            public void onTick(long millisUntilFinished) {


                marginTop += 32;

                params.setMargins(0,marginTop,0,0);

                ivLogo.setLayoutParams(params);
            }

            @Override
            public void onFinish() {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        };


        timer.start();
    }
}
