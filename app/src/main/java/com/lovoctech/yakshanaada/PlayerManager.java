package com.lovoctech.yakshanaada;

import android.content.Context;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.lovoctech.yakshanaada.model.Shruthi;

import java.util.Timer;
import java.util.TimerTask;

import static com.lovoctech.yakshanaada.service.AudioService.MEDIA_SESSION_TAG;

public class PlayerManager {
    private SimpleExoPlayer currentPlayer;
    private SimpleExoPlayer nextPlayer;
    private RxBus rxBus;
    private static PlayerManager INSTANCE;
    @Nullable
    private Shruthi shruthi;
    private Context context;
    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;


    private PlayerManager() {

    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }


    public void init(Context context, RxBus rxBus) {
        this.rxBus = rxBus;
        this.context = context;
        currentPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        nextPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
    }

    public void setShruthi(Shruthi shruthi) {

        soundpool(shruthi);
        //exo(shruthi);
    }

    private void exo(Shruthi shruthi) {
        this.shruthi = shruthi;
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getString(R.string.app_name)));
        MediaSource source = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(shruthi.getUri()));
        MediaSource nextSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(shruthi.getUri()));
        // release();
        currentPlayer.prepare(source);
        currentPlayer.setPlayWhenReady(true);

        nextPlayer.setPlayWhenReady(false);
        nextPlayer.prepare(nextSource);

        mediaSession = new MediaSessionCompat(context, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                return new MediaDescriptionCompat.Builder().build();
            }
        });
        mediaSessionConnector.setPlayer(currentPlayer, null);


        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                long currentPlayerPosition = currentPlayer.getCurrentPosition();
                long nextPlayerPosition = nextPlayer.getCurrentPosition();

                Log.d("CURent Playing", String.valueOf(currentPlayerPosition));
                Log.d("NExt Playing", String.valueOf(nextPlayerPosition));
                long duration = currentPlayer.getDuration();
                Log.d("Duratio Playing", String.valueOf(duration));
                if (duration > 0 && currentPlayerPosition > duration - 4000) {
                    nextPlayer.setPlayWhenReady(true);
                    if (currentPlayerPosition == duration - 1000) {
                        currentPlayer.seekTo(0);
                        currentPlayer.setPlayWhenReady(false);
                        currentPlayer.prepare(new ConcatenatingMediaSource(source));
                    }
                }

                if (duration > 0 && nextPlayerPosition > duration - 4000) {
                    currentPlayer.setPlayWhenReady(true);
                    if (nextPlayerPosition > duration - 1000) {
                        nextPlayer.seekTo(0);
                        nextPlayer.setPlayWhenReady(false);
                        currentPlayer.prepare(new ConcatenatingMediaSource(source));
                    }
                }

            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule(hourlyTask, 0L, 1000);

    }

    private void soundpool(Shruthi shruthi) {
       /* AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actVolume / maxVolume;

        SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> {
            int soundID = soundPool1.load(context, R.raw.d, 1);
            soundPool1.play(soundID, volume, volume, 1, -1, 1f);
        });*/
        //  new LoopMediaPlayer(context, R.raw.bili_1).start();
        PerfectLoopMediaPlayer.create(context, R.raw.testtaaa);
    }

    private void release() {
        currentPlayer.release();
        nextPlayer.release();
    }


}
