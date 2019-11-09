package com.lovoctech.yakshanaada.ui;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.lovoctech.yakshanaada.PlayerManager;
import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.RxBus;
import com.lovoctech.yakshanaada.YakshaNaadaApplication;
import com.lovoctech.yakshanaada.model.Event;
import com.lovoctech.yakshanaada.model.Shruthi;
import com.lovoctech.yakshanaada.repository.ShruthiRepository;
import com.lovoctech.yakshanaada.service.AudioService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListActivity extends AppCompatActivity {

    @BindView(R.id.shruthi_list)
    RecyclerView recyclerView;

    @BindView(R.id.shruthi_title)
    TextView shruthiTitle;

    @BindView(R.id.play_pause)
    ImageView playPauseBtn;

    @BindView(R.id.player_area)
    RelativeLayout playerArea;

    @BindView(R.id.adView)
    AdView adView;

    @OnClick(R.id.play_pause)
    void onPlayPause() {
        if (serviceBound) {
            audioService.togglePlayer();
        }
    }

    private RxBus rxBus;
    private AudioService audioService;
    private boolean serviceBound = false;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.yakshanaada);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        PlayerManager.getInstance().init(this, rxBus);
        PlayerManager.getInstance().setShruthi(ShruthiRepository.BILI_1);

        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        rxBus = ((YakshaNaadaApplication) getApplication()).bus();

        List<Shruthi> shruthis = ShruthiRepository.getShruthis();


        ShruthiAdapter shruthiAdapter = new ShruthiAdapter(shruthis, rxBus);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(shruthiAdapter);

        MobileAds.initialize(this, getString(R.string.admob_app_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        rxBus.send(new Event(null, Event.APP_STARTED));

        rxBus.toObservable()
                .subscribe(object -> {
                    if (object instanceof Shruthi) {
                        setOrStart((Shruthi) object);
                    } else if (object instanceof Event) {
                        playerEvent((Event) object);
                    }
                });
    }

    private void setPlayAreaVisibility(int visibility) {
        if (playerArea != null && playerArea.getHandler() != null) {
            playerArea.getHandler().post(() -> {
                playerArea.setVisibility(visibility);
            });
        } else {
            if (playerArea != null) {
                playerArea.getHandler();
                playerArea.setVisibility(visibility);
            }
        }

    }

    private void playerEvent(Event event) {
        if (!serviceBound) {
            startService((Shruthi) null);
            return;
        }
        int imageResId = R.drawable.pause;
        switch (event.getStatus()) {
            case Event.PAUSED:
                imageResId = R.drawable.play;
                setPlayAreaVisibility(View.VISIBLE);
                break;
            case Event.STOPPED:
                setPlayAreaVisibility(View.GONE);
                break;
            case Event.PLAYING:
                imageResId = R.drawable.pause;
                setPlayAreaVisibility(View.VISIBLE);
                break;
        }
        if (playPauseBtn != null) {
            playPauseBtn.setImageResource(imageResId);
        }
    }

    private void setOrStart(Shruthi shruthi) {
        if (serviceBound) {
            setShruthi(shruthi);
        } else {
            startService(shruthi);
        }
    }

    private void startService(Shruthi shruthi) {
        Intent intent = new Intent(this, AudioService.class);
        if (shruthi != null) {
            intent.putExtra(Shruthi.SHRUTHI, shruthi);
            shruthiTitle.setText(shruthi.getTitle());
        }
        ContextCompat.startForegroundService(this, intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setShruthi(Shruthi shruthi) {
        if (serviceBound) {
            shruthiTitle.setText(shruthi.getTitle());
            audioService.setShruthi(shruthi);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (serviceBound) {
            playerEvent(audioService.getEvent());
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioServiceBinder myBinder = (AudioService.AudioServiceBinder) service;
            audioService = myBinder.getService();
            serviceBound = true;
            playerEvent(audioService.getEvent());
        }
    };

}
