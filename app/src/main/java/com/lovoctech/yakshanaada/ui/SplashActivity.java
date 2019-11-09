package com.lovoctech.yakshanaada.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

public class SplashActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler handler = new Handler();
        handler.postDelayed(this::startListActivity, 500);


    }

    private void startListActivity() {
        Intent intent = new Intent(this, DrawerActivity.class);
        startActivity(intent);
        finish();
    }
}
