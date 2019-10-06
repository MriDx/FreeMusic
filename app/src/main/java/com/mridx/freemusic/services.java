package com.mridx.freemusic;

import android.app.Notification;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;


public class services extends Service {

    public MediaPlayer mPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mPlayer = new MediaPlayer();
        initMusicPlayer();
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        mPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Log.d("tags", "Player init");
        //set listeners

    }

    public void playSong(String url){
        //play
        mPlayer.reset();
        //set uri
        //Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currSong);
        //set the data source
        try{
            mPlayer.setDataSource(getApplicationContext(), Uri.parse(url));
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mPlayer.prepareAsync();

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                int NOTIFY_ID = 002;

                builder
                        //.setContentIntent(pendInt)
                        .setSmallIcon(R.drawable.ic_play)
                        .setTicker("Song Title")
                        .setOngoing(true)
                        .setContentTitle("Playing")
                        .setContentText("Content Text");
                Notification not = builder.build();
                startForeground(NOTIFY_ID, not);
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

    }

}
