package com.lovoctech.yakshanaada;

import android.content.Context;
import android.os.Handler;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
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

import androidx.annotation.Nullable;

import static com.lovoctech.yakshanaada.service.AudioService.MEDIA_SESSION_TAG;

public class KareokePlayer implements Player.EventListener {

    private SimpleExoPlayer currentPlayer;
    private SimpleExoPlayer nextPlayer;
    private SimpleExoPlayer sourcePlayer;
    private RxBus rxBus;
    private static KareokePlayer INSTANCE;
    @Nullable
    private Context context;
    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;
    private Handler handler;

    public static KareokePlayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KareokePlayer();
        }
        return INSTANCE;
    }

    public void init(Context context, RxBus rxBus) {
        this.rxBus = rxBus;
        this.context = context;

    }

    public void playNade(Shruthi shruthi, int nadeUri, float tempo) {
        this.playShruthi(shruthi);
        this.playNade(nadeUri, tempo);
    }

    public void playBidthige(int bidthigeUri, int nadeUri, float tempo) {
        releasePlayer();
        sourcePlayer = getPlayer(false, 1.0f, bidthigeUri, tempo);
        sourcePlayer.setPlayWhenReady(true);
        playNadeWhenDone(nadeUri, tempo);
    }

    private void releasePlayer() {
        if (sourcePlayer != null) {
            sourcePlayer.release();
            sourcePlayer = null;
        }
    }

    private void playNade(int uri, float tempo) {
        releasePlayer();
        sourcePlayer = this.getPlayer(true, 1.0f, uri, tempo);
        sourcePlayer.setPlayWhenReady(true);
    }

    private SimpleExoPlayer getPlayer(boolean isLooping, float volume, int uri, float tempo) {
        SimpleExoPlayer currentPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        mediaSession = new MediaSessionCompat(context, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                return new MediaDescriptionCompat.Builder().build();
            }
        });
        currentPlayer.setVolume(volume);
        mediaSessionConnector.setPlayer(currentPlayer, null);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getString(R.string.app_name)));
        MediaSource source = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(uri));
        PlaybackParameters p = new PlaybackParameters(tempo);
        currentPlayer.setPlaybackParameters(p);
        if (isLooping) {
            currentPlayer.prepare(new LoopingMediaSource(source));
        } else {
            currentPlayer.prepare(source);
        }
        return currentPlayer;
    }

    private void playShruthi(Shruthi shruthi) {
        currentPlayer = this.getPlayer(true, 0.05f, shruthi.getUri(), 1.0f);
        currentPlayer.setPlayWhenReady(true);
        startNextShruthi(shruthi.getUri());
    }

    private void startNextShruthi(int uri) {
        handler = new Handler();
        nextPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getString(R.string.app_name)));
        MediaSource nextSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(uri));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextPlayer.prepare(new LoopingMediaSource(nextSource));
                nextPlayer.setVolume(0.05f);
                nextPlayer.setPlayWhenReady(true);
            }
        }, 1000);
    }

    private void playNadeWhenDone(int nadeUri, float tempo) {
        sourcePlayer.addListener(new Player.EventListener() {

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        break;
                    case Player.STATE_ENDED:
                        playNade(nadeUri, tempo);
                        break;
                    case Player.STATE_IDLE:
                        break;
                    case Player.STATE_READY:
                        break;
                    default:
                        break;
                }
            }

        });
    }


}
