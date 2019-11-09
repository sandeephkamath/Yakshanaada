package com.lovoctech.yakshanaada.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;
import com.lovoctech.yakshanaada.R;
import com.lovoctech.yakshanaada.RxBus;
import com.lovoctech.yakshanaada.YakshaNaadaApplication;
import com.lovoctech.yakshanaada.model.Event;
import com.lovoctech.yakshanaada.model.Shruthi;

import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.exoplayer2.Player.STATE_ENDED;


public class AudioService extends Service {

    private SimpleExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;
    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;

    public static final String PLAYBACK_CHANNEL_ID = "playback_channel";
    public static final int PLAYBACK_NOTIFICATION_ID = 1;
    public static final String MEDIA_SESSION_TAG = "audio_demo";
    private AudioServiceBinder binder = new AudioServiceBinder();
    private Shruthi currentShruthi;
    private RxBus rxBus;


    @Override
    public void onCreate() {
        super.onCreate();
        rxBus = ((YakshaNaadaApplication) getApplication()).bus();
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        mediaSession = new MediaSessionCompat(this, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                return new MediaDescriptionCompat.Builder().build();
            }
        });
        mediaSessionConnector.setPlayer(player, null);
    }


    public void setShruthi(Shruthi shruthi) {
        if (currentShruthi == null || !currentShruthi.getMediaId().equals(shruthi.getMediaId())) {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));
            MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(RawResourceDataSource.buildRawResourceUri(shruthi.getUri()));

            ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(firstSource,
                    firstSource, firstSource, firstSource, firstSource, firstSource, firstSource, firstSource, firstSource);

            player.prepare(new LoopingMediaSource(concatenatingMediaSource));
            player.setPlayWhenReady(true);
            currentShruthi = shruthi;
            Timer timer = new Timer();
            TimerTask hourlyTask = new TimerTask() {
                @Override
                public void run() {
                    if (isPlaying()) {
                        Log.d("Playing", String.valueOf(player.getContentPosition()));
                        long position = player.getContentPosition();
                        if (position > 5000 && player != null) {
                            player.prepare(firstSource);
                        }
                    }
                }
            };

// schedule the task to run starting now and then every hour...
            timer.schedule(hourlyTask, 0L, 1000);
            setNotificationManager();
        } else {
            Event event = getEvent();
            if (event.getStatus() == Event.PAUSED) {
                togglePlayer();
            }
        }
    }

    private void setNotificationManager() {
        if (playerNotificationManager != null || currentShruthi == null)
            return;
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                this,
                PLAYBACK_CHANNEL_ID,
                R.string.playback_channel_name,
                PLAYBACK_NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        if (currentShruthi != null) {
                            return currentShruthi.getTitle();
                        }
                        return "";
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        return null;
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        if (currentShruthi != null) {
                            return currentShruthi.getTitle();
                        }
                        return "";
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        if (currentShruthi != null) {
                            return BitmapFactory.decodeResource(getResources(), currentShruthi.getBitmapResource());
                        }
                        return BitmapFactory.decodeResource(getResources(), R.drawable.yakshanaada_background);
                    }
                }
        );

        playerNotificationManager.setUseNavigationActions(false);
        playerNotificationManager.setUseChronometer(false);
        playerNotificationManager.setRewindIncrementMs(0);
        playerNotificationManager.setFastForwardIncrementMs(0);
        playerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                stopForeground(true);
                stopSelf();
            }
        });
        playerNotificationManager.setPlayer(player);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());

        rxBus.toObservable()
                .subscribe(object -> {
                    if (object instanceof Event) {
                        appEvent((Event) object);
                    }
                });


        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (rxBus != null) {
//                    if (playbackState == STATE_ENDED && player != null) {
//                        player.setPlayWhenReady(true);
//                    }
                    rxBus.send(getEvent(playWhenReady, playbackState));
                }
            }
        });
    }

    private void appEvent(Event event) {
        if (event != null && event.getStatus() == Event.APP_STARTED && rxBus != null) {
            rxBus.send(getEvent());
        }
    }

    public Event getEvent() {
        boolean playWhenReady = player.getPlayWhenReady();
        int playbackState = player.getPlaybackState();
        return getEvent(playWhenReady, playbackState);
    }

    private Event getEvent(boolean playWhenReady, int playbackState) {
        boolean isPlaying = playWhenReady && playbackState == Player.STATE_READY;
        int status;
        if (isPlaying) {
            status = Event.PLAYING;
        } else if (playWhenReady && playbackState == Player.STATE_IDLE) {
            status = Event.STOPPED;
            currentShruthi = null;
        } else {
            status = Event.PAUSED;
        }
        return new Event(currentShruthi, status);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Shruthi shruthi = intent.getParcelableExtra(Shruthi.SHRUTHI);
            if (shruthi != null) {
                setShruthi(shruthi);
            }
        }
        return START_STICKY;
    }

    private boolean isPlaying() {
        if (player != null) {
            return player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
        }
        return false;
    }

    public void togglePlayer(Shruthi shruthi) {
        if (player != null) {
            player.setPlayWhenReady(!isPlaying());
            if (player.getPlaybackState() == Player.STATE_IDLE) {
                setShruthi(shruthi);
            }
        }

    }

    public void togglePlayer() {
        if (player != null) {
            player.setPlayWhenReady(!isPlaying());
        }
    }

    @Override
    public void onDestroy() {
        if (mediaSession != null) {
            mediaSession.release();
        }
        if (mediaSessionConnector != null) {
            mediaSessionConnector.setPlayer(null, null);
        }
        if (playerNotificationManager != null) {
            playerNotificationManager.setPlayer(null);
        }
        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    public class AudioServiceBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

}
