package com.mridx.freemusic;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

import static com.mridx.freemusic.MainUI.currentPlayingName;
import static com.mridx.freemusic.MainUI.currentPlayingPosition;
import static com.mridx.freemusic.MainUI.currentPlayingUrl;
import static com.mridx.freemusic.MainUI.nowPlayingSongMainUI;
import static com.mridx.freemusic.MainUI.play;

public class MediaHandler {

    private static final String MAINCHANNEL_ID = "01";
    private MediaPlayer mediaPlayer;
    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder, playing;
    long[] pattern = {0, 200, 0};
    private int id = 010;
    private PendingIntent pendingSwitchIntent, pendingUpdate;
    private Intent switchIntent;

    public MediaHandler(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        buildNotification();
    }

    private void buildNotification() {

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //notificationID = new Random().nextInt(10);

        notificationBuilder = new NotificationCompat.Builder(context, MAINCHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Download Complete")
                .setContentText("Song download complete")
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.RED, 1, 1);
        //.setSound(defaultSoundUri);
        //.setContentIntent(pendingIntent);
        //.addAction(R.mipmap.ic_launcher_round, "Open Now", pendingIntent);

        playing = new NotificationCompat.Builder(context, MAINCHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(false)
                .addAction(R.drawable.ic_play, "Play", pendingSwitchIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }

        switchIntent = new Intent("com.mridx.freemusic.ACTION_PLAY");
        pendingSwitchIntent = PendingIntent.getBroadcast(context, 100, switchIntent, 0);


        Intent i = new Intent("com.mridx.freemusic.ACTION_PLAY");
        //i.putExtra("action", pause);
        //i.putExtra("newver", remoteMessage.getData().get("newver"));
        //i.putExtra("notId", notificationID);
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingUpdate = PendingIntent.getBroadcast(context, 100 /* request code */, i, 0);


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(MAINCHANNEL_ID, "MAIN_CHANNEL", NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription("This is main channel for notification");
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    @SuppressLint("RestrictedApi")
    public void init(String link, final String name) {

        currentPlayingUrl = link;
        currentPlayingName = name;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
        }

        nowPlayingSongMainUI.setText(name);

        playing.setContentTitle(name);
        playing.mActions.clear();
        notificationManager.notify(id, playing.build());

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(link);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                play.setImageResource(R.drawable.ic_pause);
                mediaPlayer.start();
                playing.setContentText(name).setContentTitle("Playing");
                playing.addAction(R.drawable.ic_pause, "Pause", pendingSwitchIntent);
                playing.setOngoing(true);
                notificationManager.notify(id, playing.build());
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCompletion(MediaPlayer mp) {
                notificationManager.cancel(id);
                mediaPlayer.reset();
                play.setImageResource(R.drawable.ic_play);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaPlayer.reset();
                playing.setContentTitle("Error!");
                playing.setContentText("Media Player Error!");
                playing.mActions.clear();
                playing.setOngoing(false);
                playing.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
                notificationManager.notify(id, playing.build());
                return false;

            }
        });
    }

    public void Pause() {
        notificationManager.cancel(id);
        currentPlayingPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
        play.setImageResource(R.drawable.ic_play);

    }

    @SuppressLint("RestrictedApi")
    public void Play() {
        if (currentPlayingUrl != null) {
            mediaPlayer.seekTo(currentPlayingPosition);
            mediaPlayer.start();
            play.setImageResource(R.drawable.ic_pause);
            playing.setContentTitle(currentPlayingName);
            playing.mActions.clear();
            notificationManager.notify(id, playing.build());
        } else {
            Toast.makeText(context.getApplicationContext(), "Select a song", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("RestrictedApi")
    public void playOrPause() {
        if (mediaPlayer.isPlaying()) {
            playing.mActions.remove(0);
            playing.addAction(R.drawable.ic_play, "Play", pendingSwitchIntent);
            playing.setOngoing(false);
            notificationManager.notify(id, playing.build());
            currentPlayingPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            play.setImageResource(R.drawable.ic_play);
        } else {
            if (currentPlayingUrl != null) {
                mediaPlayer.seekTo(currentPlayingPosition);
                mediaPlayer.start();
                play.setImageResource(R.drawable.ic_pause);
                playing.setContentTitle(currentPlayingName);
                playing.mActions.clear();
                playing.addAction(R.drawable.ic_pause, "Pause", pendingSwitchIntent);
                playing.setOngoing(true);
                notificationManager.notify(id, playing.build());
            } else {
                Toast.makeText(context.getApplicationContext(), "Select a song", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isplaying() {
        if (mediaPlayer.isPlaying()) {
            notificationManager.cancel(id);
            mediaPlayer.reset();
            return false;
        } else {
            return false;
        }
    }

    public void Next() {
        if (mediaPlayer.isPlaying()) {
            notificationManager.cancel(id);
            mediaPlayer.reset();
        }
        AllSongsAdapter allSongsAdapter = new AllSongsAdapter((MainUI) context.getApplicationContext());
        allSongsAdapter.getNext();

    }

    public void destroy() {

        mediaPlayer.reset();
        mediaPlayer.release();
        notificationManager.cancelAll();

    }
}
