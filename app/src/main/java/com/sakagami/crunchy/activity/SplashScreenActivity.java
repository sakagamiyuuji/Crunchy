package com.sakagami.crunchy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.sakagami.crunchy.R;

import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {

    Animation titleFromUp, titleDescFromSide;
    TextView tvTitle, tvDescTitle;
    private int loadTime = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initialComponent();
        loadAnimation();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getPackageName() + "android.net.conn.CONNECTIVITY_CHANGE");


        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, loadTime);
    }

    public void initialComponent(){
        tvTitle = findViewById(R.id.tv_title);
        tvDescTitle = findViewById(R.id.tv_desc_title);
    }

    public void loadAnimation(){
        titleFromUp = AnimationUtils.loadAnimation(this, R.anim.title_splash_fromup);
        titleDescFromSide = AnimationUtils.loadAnimation(this, R.anim.titledesc_splash_fromside);
        tvTitle.setAnimation(titleFromUp);
        tvDescTitle.setAnimation(titleDescFromSide);
    }
}