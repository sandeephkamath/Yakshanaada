package com.lovoctech.yakshanaada.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.model.Shruthi;
import com.lovoctech.yakshanaada.repository.ShruthiRepository;
import com.lovoctech.yakshanaada.service.AudioService;

public class MainActivity extends AppCompatActivity {

    private Button dButton, dsButton, eButton, fButton, gButton;
    private AudioService audioService;
    private boolean serviceBound = false;
    private View.OnClickListener onClickListener;
    private AdView adView;
    private View selectedBtton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-4879890824365927~4846271458");

        dButton = findViewById(R.id.d);
        dsButton = findViewById(R.id.ds);
        eButton = findViewById(R.id.e);
        fButton = findViewById(R.id.f);
        gButton = findViewById(R.id.g);
        adView = findViewById(R.id.adView);

        MobileAds.initialize(this, initializationStatus -> {
        });

        MobileAds.initialize(this, "ca-app-pub-4879890824365927~4846271458");

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        onClickListener = view -> {

            Shruthi shruthi = ShruthiRepository.BILI_2;
            switch (view.getId()) {
                case R.id.d:
                    shruthi = ShruthiRepository.BILI_2;
                    break;
                case R.id.ds:
                    shruthi = ShruthiRepository.KAPPU_2;
                    break;
                case R.id.e:
                    shruthi = ShruthiRepository.BILI_3;
                    break;
                case R.id.f:
                    shruthi = ShruthiRepository.BILI_4;
                    break;
                case R.id.g:
                    shruthi = ShruthiRepository.BILI_5;
                    break;
            }

            if (selectedBtton != null) {
                selectedBtton.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_bg));
                if (selectedBtton.getId() == view.getId()) {
                    if (serviceBound) {
                        audioService.togglePlayer(shruthi);
                    } else {
                        startService(shruthi);
                    }
                } else {
                    if (!serviceBound) {
                        startService(shruthi);
                    } else {
                        setShruthi(shruthi);
                    }
                }
            } else {
                if (!serviceBound) {
                    startService(shruthi);
                } else {
                    setShruthi(shruthi);
                }
            }
            selectedBtton = view;
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.button_pressed_bg));
        };


        dButton.setOnClickListener(onClickListener);
        dsButton.setOnClickListener(onClickListener);
        eButton.setOnClickListener(onClickListener);
        fButton.setOnClickListener(onClickListener);
        gButton.setOnClickListener(onClickListener);


    }

    private void startService(Shruthi shruthi) {
        if (!serviceBound) {
            Intent intent = new Intent(this, AudioService.class);
            intent.putExtra(Shruthi.SHRUTHI, shruthi);
            ContextCompat.startForegroundService(this, intent);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void setShruthi(Shruthi shruthi) {
        if (serviceBound) {
            audioService.setShruthi(shruthi);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioServiceBinder myBinder = (AudioService.AudioServiceBinder) service;
            audioService = myBinder.getService();
            serviceBound = true;
        }
    };
}
