package com.mridx.freemusic;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.mridx.freemusic.API.ALBUM_API;
import static com.mridx.freemusic.API.albumDescription;
import static com.mridx.freemusic.API.albumImage;
import static com.mridx.freemusic.API.albumTitle;
import static com.mridx.freemusic.API.playlistID;
import static com.mridx.freemusic.API.playlistImage;
import static com.mridx.freemusic.API.playlistTitle;
import static com.mridx.freemusic.API.songDescription;
import static com.mridx.freemusic.API.songID;
import static com.mridx.freemusic.API.songImage;
import static com.mridx.freemusic.API.songTitle;
import static com.mridx.freemusic.API.songs320List;
import static com.mridx.freemusic.API.songsImageList;
import static com.mridx.freemusic.API.songsList;
import static com.mridx.freemusic.API.songsUrlList;
import static com.mridx.freemusic.MainUI.currentStream;
import static com.mridx.freemusic.MainUI.mediaPlayer;

public class SongsList extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView songList;
    private String albumID;
    private ProgressDialog progressDialog;
    private GridLayoutManager gridLayoutManager;
    private MediaPlayer mediaPlayer;
    private AppCompatImageView play, next, prev;
    private AppCompatTextView nowPlayingSong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list);

        Bundle bundle = getIntent().getExtras();
        albumID = bundle.getString("albumID");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Album");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        songList = findViewById(R.id.songsList);

        this.play = MainUI.play;
        this.prev = MainUI.prev;
        this.next = MainUI.next;
        this.nowPlayingSong = MainUI.nowPlayingSongMainUI;

        StartLoadingSongs();

    }

    private void StartLoadingSongs() {

        showDialog("Searching...");

        class searchSongs extends AsyncTask<Void, Void, String> {

            String uri = ALBUM_API + albumID;
            String kbps = "96";

            @Override
            protected String doInBackground(Void... voids) {
                return new RequestHandler().sendPostRequest(uri);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String jsonData = s;
                try {
                    JSONObject fullJson = new JSONObject(jsonData.substring(jsonData.indexOf("{")));
                    if (fullJson.has("songs")) {
                        JSONArray songs = fullJson.getJSONArray("songs");
                        for (int i = 0; i < songs.length(); i++) {
                            JSONObject song = songs.getJSONObject(i);
                            songsList.add(song.getString("song"));
                            songsImageList.add(song.getString("image"));
                            songs320List.add(song.getBoolean("320kbps"));
                            if (song.getBoolean("320kbps")) {
                                kbps = "320";
                            }
                            String preview_url = song.getString("media_preview_url");
                            String url = preview_url.replace("preview", "aac").replace("96_p", kbps);
                            Log.d("url", url);
                            songsUrlList.add(url);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (songsList.size() > 0) {
                    //searchAlbumView.setVisibility(View.VISIBLE);
                    SongListAdapter songListAdapter = new SongListAdapter(SongsList.this, songsList, songsImageList, songs320List, songsUrlList);
                    gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                    songList.setLayoutManager(gridLayoutManager);
                    songList.setAdapter(songListAdapter);
                }

                /*
                SongSearchAdapter songSearchAdapter = new SongSearchAdapter(SearchUI.this, songID, songTitle, songImage, songDescription);
                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                searchUISongsHolder.setLayoutManager(gridLayoutManager);
                searchUISongsHolder.setAdapter(songSearchAdapter);
                */
                hideDialog();
            }
        }
        searchSongs searchSongs = new searchSongs();
        searchSongs.execute();


    }

    public void startPlaying(String url, final String name) {
        if (mediaPlayer ==  null) {
            this.mediaPlayer = MainUI.mediaPlayer;
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        try {
            currentStream = url;
            mediaPlayer.setDataSource(url);
            Log.d("tag", "Source has been set");
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mediaPlayer != null) {

        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    nowPlayingSong.setText(name);
                }
            }
        });

    }

    public void showDialog(String s) {
        progressDialog.setMessage(s);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.dismiss();
    }
}
