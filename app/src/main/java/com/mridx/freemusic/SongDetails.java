package com.mridx.freemusic;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.drm.DrmStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.palette.graphics.Palette;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.transition.Transition;

import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;

import java.io.File;

import static com.mridx.freemusic.DownloadManager.ISDOWNLOADING;
import static com.mridx.freemusic.MainUI.mediaHandler;
import static com.mridx.freemusic.MainUI.palette;

public class SongDetails extends AppCompatActivity {

    private AppCompatImageView imageView, songImageView;
    private String currentSong, currentAlbum, currentYear, currentSingers, currentUrl, currentImage, currentHDImage, currentLQSong, currentHDSong, currentHQSong, currentCopyright;
    private int currentDuration;
    private boolean currentKBPS;

    private Toolbar toolbar;

    private AppCompatTextView songNameView, singersNameView;
    private CardView playCurrentMusic;

    private String currentLink;
    private CoordinatorLayout songDetailsHolder;
    private StringBuilder stringBuilder;

    private Palette palette;

    private ProgressDialog progressDialog;

    private AppCompatTextView songName, singersName, albumName, released, copyright;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_detailsholder);


        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            currentSong = Jsoup.parse(bundle.getString("songName")).text();
            currentAlbum = Jsoup.parse(bundle.getString("albumName")).text();
            currentSingers = Jsoup.parse(bundle.getString("singerName")).text();
            currentImage = Jsoup.parse(bundle.getString("imageUrl")).text();
            currentUrl = Jsoup.parse(bundle.getString("songUrl")).text();
            currentYear = Jsoup.parse(bundle.getString("songDate")).text();
            currentCopyright = Jsoup.parse(bundle.getString("copyright")).text();
            currentDuration = bundle.getInt("songDur");
            currentKBPS = bundle.getBoolean("320");
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Log.d("kbps", String.valueOf(currentKBPS));

        if (currentKBPS) {
            currentHDSong = currentUrl.replace("preview", "aac").replace("96_p", "320");
        }

        currentHQSong = currentUrl.replace("preview", "aac").replace("96_p", "160");
        currentLQSong = currentUrl.replace("preview", "aac").replace("96_p", "96");


        currentHDImage = currentImage.replace("150x150", "500x500");

        imageView = findViewById(R.id.collapsing_toolbar_image_view);
        Picasso.get().load(currentHDImage).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                /*
                if (Pojo.getPosterPalette()!=null){
                    setUpInfoBackgroundColor(songDetailsHolder , Pojo.getPosterPalette());

                }
                else {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            Pojo.setPosterPalette(palette);

                            setUpInfoBackgroundColor(songDetailsHolder, palette);

                        }
                    });
                } */
            }

            @Override
            public void onError(Exception e) {

            }
        });

        songImageView = findViewById(R.id.songImageView);
        //Picasso.get().load(currentImage).into(songImageView);
        songNameView = findViewById(R.id.songNameView);
        songNameView.setText(currentSong);
        singersNameView = findViewById(R.id.singersNameView);
        singersNameView.setText(currentSingers);

        songDetailsHolder = findViewById(R.id.songDetailsHolder);

        playCurrentMusic = findViewById(R.id.playCurrentMusic);
        playCurrentMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignalToPlay();
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenTrackSeletion();
            }
        });

        int min = currentDuration / 60;
        int sec = currentDuration % 60;
        if (sec <= 9) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(min);
            stringBuilder.append(":0");
            stringBuilder.append(sec);
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append(min);
            stringBuilder.append(":");
            stringBuilder.append(sec);
        }

        Log.d("dur", stringBuilder.toString());

        songName = findViewById(R.id.songDetailsName);
        songName.setText(currentSong);
        albumName = findViewById(R.id.albumDetailsName);
        albumName.setText(currentAlbum);
        singersName = findViewById(R.id.singersDetailsName);
        singersName.setText(currentSingers);
        released = findViewById(R.id.released);
        released.setText(currentYear);
        copyright = findViewById(R.id.copyright);
        copyright.setText(currentCopyright);


    }

    private void OpenTrackSeletion() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.track_selection_btnsheet);
        bottomSheetDialog.show();

        ConstraintLayout card1 = bottomSheetDialog.findViewById(R.id.card1);
        ConstraintLayout card2 = bottomSheetDialog.findViewById(R.id.card2);
        ConstraintLayout card3 = bottomSheetDialog.findViewById(R.id.card3);

        if (!currentKBPS) {
            assert card1 != null;
            card1.setVisibility(View.GONE);
        }

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "320 Kbps", Toast.LENGTH_SHORT).show();
                //StartDownload(currentHDSong);
                currentLink = currentHDSong;
                bottomSheetDialog.dismiss();
                CheckPermission();
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "160 Kbps", Toast.LENGTH_SHORT).show();
                //StartDownload(currentHQSong);
                currentLink = currentHQSong;
                bottomSheetDialog.dismiss();
                CheckPermission();
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "90 Kbps", Toast.LENGTH_SHORT).show();
                //StartDownload(currentLQSong);
                currentLink = currentLQSong;
                bottomSheetDialog.dismiss();
                CheckPermission();
            }
        });

    }

    private void StartDownload() {
        final String ext = "m4a";
        if (!ISDOWNLOADING) {
            final DownloadManager downloadManager = new DownloadManager(this);
            downloadManager.StartDownload(currentLink, currentSong, ext, currentImage, currentAlbum, currentSingers, currentYear, currentHDImage);
        } else {
            Snackbar.make(songDetailsHolder, "Concurrent Downloading is not allowed. Try after current download finish.", Snackbar.LENGTH_SHORT).show();
        }

    }

    private void SignalToPlay() {
        //Intent intent = new Intent();
        //intent.setAction();
        //ComponentName comp = new ComponentName("com.android.music", "com.android.music.MediaPlaybackActivity");
        //intent.setComponent(comp);
        File file = null;
        String url = null;
        if (currentKBPS) {
            //file = new File(currentHDSong);
            url = currentHDSong;
            mediaHandler.init(currentHDSong, currentSong);
        } else {
            mediaHandler.init(currentHQSong, currentSong);
            //file = new File(currentHQSong);
            //url = currentHQSong;
        }
        //services media = new services();
        //media.playSong(currentHDSong);

        //intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
        //intent.setDataAndType(Uri.parse(url), "audio/mp3");
        //startActivity(intent);

    }

    private void CheckPermission() {
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
                        startActivityForResult(intent, 01);
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
            StartDownload();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StartDownload();
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Permission Denied");
                    alertDialog.setMessage("App must have storage permission to download the update");
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
        }
    }

    private void setUpInfoBackgroundColor(CoordinatorLayout coordinatorLayout, Palette palette) {
        Palette.Swatch swatch = getMostPopulousSwatch(palette);
        if(swatch != null){
            int startColor = ContextCompat.getColor(coordinatorLayout.getContext(), R.color.gray);
            int endColor = swatch.getRgb();

            int[] colors = {startColor, endColor};

            //AnimationUtility.animateBackgroundColorChange(coordinatorLayout, startColor, endColor);
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
            gradientDrawable.setCornerRadius(0f);
            //coordinatorLayout.setBackground(gradientDrawable);
            getWindow().getDecorView().setBackground(gradientDrawable);

        }
    }

    Palette.Swatch getMostPopulousSwatch(Palette palette) {
        Palette.Swatch mostPopulous = null;
        if (palette != null) {
            for (Palette.Swatch swatch : palette.getSwatches()) {
                if (mostPopulous == null || swatch.getPopulation() > mostPopulous.getPopulation()) {
                    mostPopulous = swatch;
                }
            }
        }
        return mostPopulous;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }
}
