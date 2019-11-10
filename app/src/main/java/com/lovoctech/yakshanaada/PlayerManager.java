package com.lovoctech.yakshanaada;

import android.content.Context;
import android.os.Handler;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.lovoctech.yakshanaada.model.Shruthi;

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
    private Handler handler;


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

    }

    public void setShruthi(Shruthi shruthi) {
        exo(shruthi);
    }

    private void exo(Shruthi shruthi) {
        this.shruthi = shruthi;
        unsetHandler();
        currentPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        nextPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
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


        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getString(R.string.app_name)));
        MediaSource source = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(shruthi.getUri()));
        MediaSource nextSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(shruthi.getUri()));


        currentPlayer.prepare(new LoopingMediaSource(source));
        currentPlayer.setPlayWhenReady(true);

        startNext(nextSource);


    }

    private void startNext(MediaSource nextSource) {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextPlayer.prepare(new LoopingMediaSource(nextSource));
                nextPlayer.setPlayWhenReady(true);
            }
        }, 1000);
    }

    private void unsetHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public boolean isPlaying() {
        if (currentPlayer != null) {
            return currentPlayer.getPlaybackState() == Player.STATE_READY && currentPlayer.getPlayWhenReady();
        }
        return false;
    }

    public void pause() {
        pause(currentPlayer);
        pause(nextPlayer);
        unsetHandler();
    }

    public void resume() {
        resume(nextPlayer);
        resume(currentPlayer);
    }

    public void release() {
        release(currentPlayer);
        release(nextPlayer);
    }

    private void release(ExoPlayer player) {
        if (player != null) {
            player.release();
        }
    }

    private void pause(ExoPlayer player) {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    private void resume(ExoPlayer player) {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }


}
