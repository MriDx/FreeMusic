package com.mridx.freemusic;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.snackbar.Snackbar;
import com.tonyodev.fetch2.Downloader;
import com.tonyodev.fetch2.Func;

import org.apache.http.util.ByteArrayBuffer;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.AndroidArtwork;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.Environment.getExternalStorageDirectory;
import static com.mridx.freemusic.MainUI.mainFetch;

public class DownloadManager {

    private Context context;
    private ProgressDialog progressDialog;
    private NotificationManager notificationManager;
    private static final String MAINCHANNEL_ID = "01";
    private long[] pattern = {0,1000,0};
    private long[] downloadStart = {0, 200, 0};
    private long[] downloadComplete = {0, 200, 0};
    private long[] loading = {0, 0};
    private int notificationID;
    private NotificationCompat.Builder notificationBuilder, downloading;
    public static boolean ISDOWNLOADING;

    private com.tonyodev.fetch2.Request request;

    public DownloadManager(Context context) {

        this.context = context;

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //notificationID = new Random().nextInt(10);

        notificationBuilder = new NotificationCompat.Builder(context, MAINCHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Download Complete")
                .setContentText("Song download complete")
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.RED,1,1);
                //.setSound(defaultSoundUri);
                //.setContentIntent(pendingIntent);
        //.addAction(R.mipmap.ic_launcher_round, "Open Now", pendingIntent);

        downloading = new NotificationCompat.Builder(context, MAINCHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                //.setContentTitle("Downloading")
                .setAutoCancel(true)
                .setOnlyAlertOnce(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(100, 0, true);
                //.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
        //notificationManager.notify(notificationID /* ID of notification */, notificationBuilder.build());



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(MAINCHANNEL_ID, "MAIN_CHANNEL", NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription("This is main channel for notification");
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        //adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    public String Download (String link, final String name, final String ext, final String currentImage, final String currentAlbum, final String currentSingers, final String currentYear, final String currentHDImage) {

        final File file = new File(getExternalStorageDirectory() + "/Music/" + name + "." + ext);
        request = new com.tonyodev.fetch2.Request(link, file.getAbsolutePath());
        return null;

    }

    public String StartDownload(String link, final String name, final String ext, final String currentImage, final String currentAlbum, final String currentSingers, final String currentYear, final String currentHDImage) {

        ISDOWNLOADING = true;
        notificationID = (int) System.currentTimeMillis();
        Log.d("kela", "bush khn " + notificationID);

        /*
        progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Downloading...");
        progressDialog.show();
        */

        downloading.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", null);
        downloading.setContentTitle("Downloading " + name);
        //downloading.setVibrate(downloadStart);
        downloading.setPriority(NotificationCompat.PRIORITY_LOW);
        notificationManager.notify(notificationID, downloading.build());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(link)
                .build();
        final File file = new File(getExternalStorageDirectory() + "/Music/" + name + "." + ext);

        OutputStream stream = null;
        ContextWrapper contextWrapper = new ContextWrapper(context);

        try {
            stream = new FileOutputStream(getExternalStorageDirectory() + "/Music/" + name + "." + ext);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        final OutputStream finalStream = stream;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                notificationManager.cancel(notificationID);
                ISDOWNLOADING = false;
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] data = new byte[8192];
                float total = 0;
                int read_bytes = 0;
                float fileSize = response.body().contentLength();

                InputStream inputStream = response.body().byteStream();

                while ((read_bytes = inputStream.read(data)) != -1) {
                    total = total + read_bytes;
                    finalStream.write(data, 0, read_bytes);
                    //progressDialog.setProgress((int) ((total / fileSize) * 95));
                    downloading.setProgress(100, (int) ((total / fileSize) * 95), false);
                    //downloading.setVibrate(loading);
                    downloading.setPriority(NotificationCompat.PRIORITY_LOW);
                    notificationManager.notify(notificationID, downloading.build());

                    Log.d("tag", "Stopped at 95" + notificationID);
                }

                //progressDialog.dismiss();
                downloading.setProgress(100, 100, true);
                downloading.mActions.clear();
                downloading.setPriority(NotificationCompat.PRIORITY_LOW);
                notificationManager.notify(notificationID, downloading.build());
                Log.d("tag", "Stopped at 100 " + notificationID );
                inputStream.close();
                finalStream.close();
                //notificationManager.cancel(notificationID);
                Log.d("tag", "Stream closed");

                if (SetTags(name, currentAlbum, currentSingers, currentYear, currentHDImage, file)) {
                    //Toast.makeText(context, "Downloaded to " + getExternalStorageDirectory() + "/Music/ Flolder.", Toast.LENGTH_SHORT).show();
                    //progressDialog.setProgress(100);
                    //progressDialog.dismiss();
                    downloading.setOngoing(false);
                    downloading.setContentText("Download Complete");
                    downloading.setProgress(100, 100, false);
                    downloading.setVibrate(downloadComplete);
                    downloading.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    notificationManager.notify(notificationID, downloading.build());
                    ISDOWNLOADING = false;
                } else {
                    //progressDialog.setProgress(100);
                    //progressDialog.dismiss();
                    downloading.setOngoing(false);
                    downloading.setContentText("Download Complete");
                    downloading.setProgress(100, 100, false);
                    downloading.setVibrate(downloadComplete);
                    downloading.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    notificationManager.notify(notificationID, downloading.build());
                    ISDOWNLOADING = false;
                    //Toast.makeText(context,"Downloaded to " + getExternalStorageDirectory() + "/Music/ Flolder without Tags.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return null;

    }


    private void getImageBinary(String name, String ext, String currentImage) throws IOException {

        URL u = new URL(currentImage);
        InputStream openStream = null;
        try {
            openStream = u.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int contentLength = openStream.available();
        byte[] binaryData = new byte[contentLength];
        openStream.read(binaryData);
        openStream.close();

        //SetTags(name, ext, currentImage, openStream);

    }

    /*
    private void convertSong(final String name, final String ext, String currentImage) {
        File file = new File(getExternalStorageDirectory() + "/Music/" + name + "." + ext);

        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                String mp3 = "mp3";
                //SetTags(name, mp3);
            }

            @Override
            public void onFailure(Exception error) {
                Log.d("error", "Failed, MEssage  " + error.getMessage());
                error.getMessage();
            }
        };

        AndroidAudioConverter.with(context)
                .setFile(file)
                .setFormat(AudioFormat.MP3)
                .setCallback(callback)
                .convert();


    }
    */

    private byte[] getByteArrayImage(String url){
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            return baf.toByteArray();
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
        }
        return null;
    }

    public boolean SetTags(String currentSong, String currentAlbum, String currentSingers, String currentYear, String currentHDImage, File file) {

        //File file = new File(getExternalStorageDirectory() + "/Music/" + name + "." + ext);
        //String ARTIST = "Mridul Baishya";
        boolean TagSet = false;
        byte[] bitmapdata = getByteArrayImage(currentHDImage);
        if (file != null) {
            try {
                AudioFile audioFile = AudioFileIO.read(file);
                Mp4Tag mp4Tag = (Mp4Tag) audioFile.getTag();
                mp4Tag.setField(Mp4FieldKey.ALBUM, currentAlbum);
                mp4Tag.setField(Mp4FieldKey.ARTIST, currentSingers);
                //mp4tag.setField(Mp4FieldKey.ALBUM_ARTIST, ALBUM_ARTISTS);
                mp4Tag.setField(Mp4FieldKey.DAY, currentYear);
                //mp4tag.setField(Mp4FieldKey.COMPOSER, COMPOSER);
                // mp4tag.setField(Mp4FieldKey.DESCRIPTION, DESCR);
                // mp4tag.setField(Mp4FieldKey.COPYRIGHT, COPYR);
                mp4Tag.setField(Mp4FieldKey.TITLE, currentSong);
                AndroidArtwork cover = new AndroidArtwork();
                cover.setBinaryData(bitmapdata);
                mp4Tag.setField(cover);
                audioFile.commit();
                Log.d("Tag", "Tag added");
                return TagSet = true;


            } catch (CannotReadException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TagException e) {
                e.printStackTrace();
            } catch (ReadOnlyFileException e) {
                e.printStackTrace();
            } catch (InvalidAudioFrameException e) {
                e.printStackTrace();
            } catch (CannotWriteException e) {
                e.printStackTrace();
            }
        }
        return TagSet;

    }

}
