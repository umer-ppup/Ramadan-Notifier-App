package com.atrule.ramadannotifier.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.atrule.ramadannotifier.R;

public class SplashActivity extends AppCompatActivity {
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region view setting
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //endregion

        //region screen delayed handler
        //region declarations
        int SPLASH_DISPLAY_LENGTH = 3000;
        new Handler().postDelayed(() -> {
            SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
            if(sharedpreferences.contains("file_name")){
                Intent mainIntent = new Intent(SplashActivity.this, RamadanActivity.class);
                startActivity(mainIntent);
                finish();
            }
            else{
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
        //endregion
    }
}