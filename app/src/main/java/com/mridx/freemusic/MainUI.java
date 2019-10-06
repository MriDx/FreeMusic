package com.mridx.freemusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.tonyodev.fetch2.Fetch;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.mridx.freemusic.API.AllSongsName;
import static com.mridx.freemusic.API.AllSongsUri;
import static com.mridx.freemusic.API.albumart;
import static com.mridx.freemusic.API.artist;
import static com.mridx.freemusic.API.song;

public class MainUI extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private Context context;
    private FloatingActionButton fab;

    private RecyclerView mainUISongsHolder;
    //private LinearLayoutManager linearLayoutManager;

    String[] extensions = { "mp3", "m4a", "MP3", "M4A" };
    private String newVer;

    public static AppCompatImageView play, next, prev;
    public static MediaPlayer mediaPlayer;
    public static AppCompatTextView nowPlayingSongMainUI;
    public static String currentStream = null;

    private String homedir = Environment.getExternalStorageDirectory().getPath() + "/Music/";

    public static Palette palette;
    @SuppressLint("StaticFieldLeak")
    public static MediaHandler mediaHandler;
    private AppCompatTextView mainUITitle;
    private CardView nowPlayingMainUI;
    private AppCompatImageView splashImage;
    private ConstraintLayout splash, nosongs;
    private Animation animation_1, animation_2, animation_3, fadeOut, fadeIn;
    public static String currentPlayingUrl, currentPlayingName;
    public static int currentPlayingPosition, adapterPostion;

    private ProgressDialog progressDialog;

    private FirebaseAnalytics mFirebaseAnalytics;

    private CoordinatorLayout mainLayout;

    public static Fetch mainFetch;


    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mainUISongsHolder = findViewById(R.id.mainUISongsHolder);

        mediaHandler = new MediaHandler(this);


        animation_1 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate);
        animation_1.setRepeatCount(Animation.INFINITE);
        animation_2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.autorotate);
        animation_2.setRepeatCount(Animation.INFINITE);
        animation_3 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fade_out);
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(1000);
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setStartOffset(0);
        fadeIn.setDuration(1000);

        splashImage = findViewById(R.id.splashImage);
        splash = findViewById(R.id.splash);
        nosongs = findViewById(R.id.noSongs);
        //splashImage.setAnimation(animation_1);
        animation_2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImage.setAnimation(animation_2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation_1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImage.setAnimation(animation_2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashImage.setAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mainFetch = new Fetch.Builder(this, "Main")
                .setDownloadConcurrentLimit(3)
                .enableLogging(true)
                .enableRetryOnNetworkGain(true)
                .build();



        startService(new Intent(this, services.class));

        //mediaService = new services();

        mainUITitle = findViewById(R.id.mainUITitle);
        nowPlayingMainUI = findViewById(R.id.nowPlayinMainUI);
        mainLayout = findViewById(R.id.mainLayout);

        mediaPlayer = new MediaPlayer();

        play = findViewById(R.id.playPauseMainUI);
        prev = findViewById(R.id.prevMainUI);
        next = findViewById(R.id.nextMainUI);
        nowPlayingSongMainUI = findViewById(R.id.nowPlayingSongMainUI);

        //loadmp3(homedir);
        CheckPermission();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if (mediaPlayer.isPlaying()) {
                    mediaHandler.Pause();
                    //mediaPlayer.pause();
                    //play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                } else {
                    //mediaPlayer.start();
                    mediaHandler.Play();
                    //play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                }*/

                mediaHandler.playOrPause();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaHandler.isplaying()) {
                    AllSongsAdapter allSongsAdapter = new AllSongsAdapter(MainUI.this);
                    allSongsAdapter.getNext();
                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaHandler.isplaying()) {
                    AllSongsAdapter allSongsAdapter = new AllSongsAdapter(MainUI.this);
                    allSongsAdapter.getPrev();
                }
            }
        });

        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play);
        if (myBitmap != null && !myBitmap.isRecycled()) {
            palette = Palette.from(myBitmap).generate();
        }

        /*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File filename = new File(Environment.getExternalStorageDirectory()+"/freemusic/logs.txt");
            if (!filename.exists()) {
                filename.mkdir();
            }
            String cmd = "logcat -d -v time -f" + filename.getAbsolutePath();
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */


    }

    private void loadmp3(final String homedir) {

        class loadSongs extends AsyncTask<Void, Void, Integer> {

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Integer doInBackground(Void... voids) {

                Drawable drawable = getDrawable(R.drawable.defaultalbum);
                assert drawable != null;
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitMapData = stream.toByteArray();

                File file = new File(homedir);
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File f : files) {
                            if (f.isDirectory()) {
                                loadmp3(f.getAbsolutePath());
                            } else {
                                for (int i = 0; i < extensions.length; i++) {
                                    if (f.getAbsolutePath().endsWith(extensions[i])) {
                                        AllSongsUri.add(f.getAbsolutePath());
                                        String song = f.getName().replace("." + extensions[i], "");
                                        AllSongsName.add(song);
                                        mediaMetadataRetriever.setDataSource(f.getAbsolutePath());
                                        String m = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                                        //Log.d("t", m);
                                        artist.add(m);
                                        byte[] art = mediaMetadataRetriever.getEmbeddedPicture();
                                        if (art != null) {
                                            albumart.add(art);
                                        } else {
                                            albumart.add(bitMapData);
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                return AllSongsUri.size();
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                //splashImage.setVisibility(View.GONE);
                splashImage.setVisibility(View.GONE);
                if (integer > 0) {
                    mainUITitle.setVisibility(View.VISIBLE);
                    AllSongsAdapter allSongsAdapter = new AllSongsAdapter(MainUI.this, AllSongsUri, AllSongsName, artist, albumart);
                    GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                    mainUISongsHolder.setLayoutManager(linearLayoutManager);
                    mainUISongsHolder.setAdapter(allSongsAdapter);
                    //mainUISongsHolder.smoothScrollToPosition(0);
                } else {
                    nosongs.setVisibility(View.VISIBLE);
                }
                //getSongList();
                getSupportActionBar().show();
                nowPlayingMainUI.setVisibility(View.VISIBLE);
            }
        }

        loadSongs loadSongs = new loadSongs();
        loadSongs.execute();

    }

    public void getSongList(){
        //query external audio

        class songList extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                ContentResolver musicResolver = getContentResolver();

                String selection=MediaStore.Audio.Media.DATA + " like?";
                String path = Environment.getExternalStorageDirectory() + "/Music/";
                String[] selectionArgs=new String[]{path};
                //videocursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, parameters, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC");
                Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                Uri uri = MediaStore.Audio.Media.getContentUri(path);

               // String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.DATA + " LIKE '"+uri+"/%'";

                Cursor musicCursor = musicResolver.query(musicUri, null, selection, selectionArgs, null);
                //iterate over results if valid
                if(musicCursor!=null && musicCursor.moveToFirst()){
                    //get columns
                    int titleColumn = musicCursor.getColumnIndex
                            (android.provider.MediaStore.Audio.Media.TITLE);
                    int idColumn = musicCursor.getColumnIndex
                            (android.provider.MediaStore.Audio.Media._ID);
                    int artistColumn = musicCursor.getColumnIndex
                            (android.provider.MediaStore.Audio.Media.ARTIST);
                    int albumartColumn = musicCursor.getColumnIndex(
                            MediaStore.Audio.Albums.ALBUM_ART);
                    Log.d("tags", "ss" + albumartColumn);
                    //add songs to list
                    do {
                        long thisId = musicCursor.getLong(idColumn);
                        String thisTitle = musicCursor.getString(titleColumn);
                        String thisArtist = musicCursor.getString(artistColumn);
                        //songList.add(new Song(thisId, thisTitle, thisArtist));
                        song.add(musicCursor.getString(titleColumn));
                        artist.add(musicCursor.getString(artistColumn));
                    }
                    while (musicCursor.moveToNext());

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                song.size();
                artist.size();
            }
        }
        songList s = new songList();
        s.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Ex. Emiway or Jump Kar");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery("", false);
                Intent i = new Intent(getApplicationContext(), SearchUI.class);
                i.putExtra("query", query);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return true;
    }

    public void PlayPause(String uri, String name, int position) {
        adapterPostion = position;
        mediaHandler.init(uri, name);
    }

    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setMessage("Please allow storage permission to get the songs from your internal storage," +
                        " from App's Settings. Click below");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Go To Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, 01);
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        loadmp3(homedir);
                    }
                });
                alertDialog.show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        101
                );
                return;
            }
        } else {
            isExist();
            loadmp3(homedir);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadmp3(homedir);

                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Permission Denied");
                    alertDialog.setMessage("App must have storage permission to; Actually you know the reason.");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            loadmp3(homedir);
                        }
                    });
                    alertDialog.show();
                }
                break;
            case 102:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadUpdate(newVer);

                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Permission Denied");
                    alertDialog.setMessage("App must have storage permission for, Actually you know the reason.");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 01:
                Log.d("rst", String.valueOf(resultCode));
                CheckPermission();
                break;
            case 02:
                Log.d("rst", String.valueOf(resultCode));
                DownloadUpdate(newVer);
                break;
        }
    }

    private boolean isExist() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/Music/");

        if (!dir.exists()) {
            dir.mkdirs();
            return true;
        } else {
            return true;
        }
    }

    private void AskToDownload(final String ver) {

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Update Found");
        alertDialog.setMessage("A newer verison is available");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Download Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownloadUpdate(ver);
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //hideNav();
            }
        });

    }

    private void DownloadUpdate(String ver) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setMessage("You denied us to access storage. " +
                        "Without storage permission update can not be downloaded. " +
                        "Kindly allow storage permission from App Settings and Try Updating");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Go To Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, 02);
                    }
                });
                alertDialog.show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102
                );
                return;
            }

        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Downloading...");
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();


            String url = "https://firebasestorage.googleapis.com/v0/b/axomitv-ca999.appspot.com/o/freemusic" + ver + ".apk?alt=media";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url)
                    .build();

            OutputStream stream = null;
            ContextWrapper contextWrapper = new ContextWrapper(this);

            try {
                stream = new FileOutputStream( contextWrapper.getFilesDir() + "/new.apk");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            final OutputStream finalStream = stream;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    byte[] data = new byte[8192];
                    float total = 0;
                    int read_bytes = 0;
                    float fileSize = response.body().contentLength();
                    float inmb = fileSize / 1000000;
                    Log.d("size", "File size is " + inmb);
                    progressDialog.setMax((int) inmb);

                    InputStream inputStream = response.body().byteStream();

                    while ((read_bytes = inputStream.read(data)) != -1) {
                        total = total + read_bytes;
                        finalStream.write(data, 0, read_bytes);
                        progressDialog.setProgress((int) ((total / 1000000)* 1));
                    }

                    progressDialog.dismiss();
                    inputStream.close();
                    finalStream.close();

                    installApp();
                }


            });

        }

    }

    private void installApp() {


        ContextWrapper contextWrapper = new ContextWrapper(this);
        File file = new File(contextWrapper.getFilesDir() +"/new.apk");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(FileProvider.getUriForFile(this,
                    "com.mridx.freemusic.FileProvider",
                    file ),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            finish();
        } else {
            Uri apkUri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.navEmail) {
            SendEmail();
        }  else if (id == R.id.navChkUp) {
            CheckUpdate();
        }
        /*else if (id == R.id.navAbtDev) {
            AboutDev();
        }*/
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void CheckUpdate() {

        showDialog("Checking for update...");

        final String uri = "https://mridx.github.io/FCMHelper/freemusic/version.txt";

        class checkUpdate extends AsyncTask<Void, Void, StringBuilder> {

            private StringBuilder result = new StringBuilder();

            @Override
            protected StringBuilder doInBackground(Void... voids) {

                try {
                    URL url = new URL(uri);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String str;
                        while ((str = in.readLine()) != null) {
                            result.append(str);
                        }
                        in.close();

                        Log.d("res", result.toString());
                    }

                    String bush = "bush khan hoi ase xt";

                } catch (MalformedURLException ignored) {
                } catch (IOException ignored) {
                }
                return result;
            }

            @Override
            protected void onPostExecute(StringBuilder s) {
                super.onPostExecute(s);
                hideDialog();
                //String ver = s.toString();
                newVer = s.toString();
                Log.d("ver", newVer);
                String current = BuildConfig.VERSION_NAME;
                if (!current.equals(newVer)) {
                    AskToDownload(newVer);
                } else {
                    Snackbar.make(mainLayout, "You are on latest version", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }

            }
        }
        checkUpdate checkUpdate = new checkUpdate();
        checkUpdate.execute();

    }

    @SuppressLint("IntentReset")
    private void SendEmail() {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/plain");
        //i.putExtra(Intent.EXTRA_SUBJECT, "Mail Subject");
        i.setData(Uri.parse("mailto:mridulbaishya28@gmail.com"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    public void showDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaHandler.destroy();
    }
}
