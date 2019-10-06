package com.mridx.freemusic;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.mridx.freemusic.fragments.Albums;
import com.mridx.freemusic.fragments.Playlists;
import com.mridx.freemusic.fragments.Songs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.mridx.freemusic.API.ALBUM_API;
import static com.mridx.freemusic.API.GENERATE_AUTH;
import static com.mridx.freemusic.API.SEARCH_API;
import static com.mridx.freemusic.API.albumDescription;
import static com.mridx.freemusic.API.albumID;
import static com.mridx.freemusic.API.albumImage;
import static com.mridx.freemusic.API.albumMusic;
import static com.mridx.freemusic.API.albumTitle;
import static com.mridx.freemusic.API.currentSongHD;
import static com.mridx.freemusic.API.currentSongName;
import static com.mridx.freemusic.API.currentSongUrl;
import static com.mridx.freemusic.API.jsonAlbumUrl;
import static com.mridx.freemusic.API.jsonSongEncUrl;
import static com.mridx.freemusic.API.jsonSongImage;
import static com.mridx.freemusic.API.jsonSongSinger;
import static com.mridx.freemusic.API.jsonSongTitle;
import static com.mridx.freemusic.API.jsonSongsID;
import static com.mridx.freemusic.API.playlistID;
import static com.mridx.freemusic.API.playlistImage;
import static com.mridx.freemusic.API.playlistTitle;
import static com.mridx.freemusic.API.songDescription;
import static com.mridx.freemusic.API.songID;
import static com.mridx.freemusic.API.songImage;
import static com.mridx.freemusic.API.songTitle;
import static com.mridx.freemusic.MainUI.currentStream;

public class SearchUI extends AppCompatActivity {

    private Toolbar toolbar;
    private String searchQuery;
    private RecyclerView searchUISongsHolder, searchUIAlbumHolder, searchUIPlaylistHolder;
    private GridLayoutManager gridLayoutManager;
    private ProgressDialog progressDialog;
    private ConstraintLayout searchAlbumView, searchPlaylistView, showSearchError;
    private Map<String, String> cookies = null;
    private String currentSong, currentAlbum, currentYear, currentSingers, currentUrl, currentImage, currentCopyright;
    private int currentDuration;
    private boolean currentKBPS;
    private RecyclerView allsongs;
    private ContentLoadingProgressBar progressBar;
    private boolean HASMORE;
    private String moreLink;
    private static String baseLink = "https://www.jiosaavn.com";
    private ViewPager viewHolder;
    private TabLayout tabLayout;

    public interface OnBottomReachedListener {

        void onBottomReached(int position);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_ui);

        Bundle bundle = getIntent().getExtras();
        searchQuery = bundle.getString("query");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        /*
        searchPlaylistView = findViewById(R.id.searchPlaylistView);
        searchAlbumView = findViewById(R.id.searchAlbumView);
        showSearchError = findViewById(R.id.showSearchError);

        //searchUISongsHolder = findViewById(R.id.searchUIPlaylistHolder);
        searchUIAlbumHolder = findViewById(R.id.searchUIAlbumHolder);
        searchUIPlaylistHolder = findViewById(R.id.searchUIPlaylistHolder);
        */

        progressBar = findViewById(R.id.toolbarLoading);

        //SearchWithJsoup(false, searchQuery, 0);
        SearchWithJsoup(false, searchQuery);

        /*
        viewHolder = (ViewPager) findViewById(R.id.viewHolder);
        setupViewPager(viewHolder);
        viewHolder.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("page", "page scrolled");

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("page", "page selected kela xt");
                Toast.makeText(getApplicationContext(), "Page Changed", Toast.LENGTH_SHORT).show();
                SearchWithJsoup(false, searchQuery, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("page", "page state changed");
            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewHolder);

        int pos = viewHolder.getCurrentItem();
        Log.d("posi", "position " + pos);

        */

    }

    private void setupViewPager(ViewPager viewPager) {

        ViewHolderAdapter adapter = new ViewHolderAdapter(getSupportFragmentManager());
        adapter.addFragment(new Songs(), "Songs");
        adapter.addFragment(new Albums(), "Albums");
        adapter.addFragment(new Playlists(), "Playlists");
        viewPager.setAdapter(adapter);

    }

    private void StartSearching() {

        showDialog("Searching...");

        class searchSongs extends AsyncTask<Void, Void, String> {

            String uri = SEARCH_API + searchQuery;

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
                    if (fullJson.has("albums")) {
                        JSONObject albums = fullJson.getJSONObject("albums");
                        JSONArray albumsData = albums.getJSONArray("data");
                        for (int i = 0; i < albumsData.length(); i++) {
                            JSONObject album = albumsData.getJSONObject(i);
                            albumID.add(album.getString("id"));
                            albumTitle.add(album.getString("title"));
                            albumImage.add(album.getString("image"));
                            albumMusic.add(album.getString("music"));
                            albumDescription.add(album.getString("description"));
                        }
                    }
                    if (fullJson.has("songs")) {
                        JSONObject songs = fullJson.getJSONObject("songs");
                        JSONArray songsData = songs.getJSONArray("data");
                        for (int i = 0; i < songsData.length(); i++) {
                            JSONObject album = songsData.getJSONObject(i);
                            songID.add(album.getString("id"));
                            songTitle.add(album.getString("title"));
                            songImage.add(album.getString("image"));
                            //songMusic.add(album.getString("music"));
                            songDescription.add(album.getString("description"));
                        }
                    }
                    if (fullJson.has("playlists")) {
                        JSONObject playlists = fullJson.getJSONObject("playlists");
                        JSONArray playlistData = playlists.getJSONArray("data");
                        for (int i = 0; i < playlistData.length(); i++) {
                            JSONObject playlist = playlistData.getJSONObject(i);
                            playlistID.add(playlist.getString("id"));
                            playlistTitle.add(playlist.getString("title"));
                            playlistImage.add(playlist.getString("image"));
                            //songMusic.add(album.getString("music"));
                            //songDescription.add(playlist.getString("description"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (albumID.size() > 0) {
                    searchAlbumView.setVisibility(View.VISIBLE);
                    AlbumSearchAdapter albumSearchAdapter = new AlbumSearchAdapter(SearchUI.this, albumID, albumTitle, albumImage, albumDescription);
                    gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                    searchUIAlbumHolder.setLayoutManager(gridLayoutManager);
                    searchUIAlbumHolder.setAdapter(albumSearchAdapter);
                }

                /*
                SongSearchAdapter songSearchAdapter = new SongSearchAdapter(SearchUI.this, songID, songTitle, songImage, songDescription);
                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                searchUISongsHolder.setLayoutManager(gridLayoutManager);
                searchUISongsHolder.setAdapter(songSearchAdapter);
                */
                if (playlistID.size() > 0) {
                    searchPlaylistView.setVisibility(View.VISIBLE);
                    SearchPlaylistAdapter searchPlaylistAdapter = new SearchPlaylistAdapter(SearchUI.this, playlistID, playlistTitle, playlistImage);
                    gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                    searchUIPlaylistHolder.setLayoutManager(gridLayoutManager);
                    searchUIPlaylistHolder.setAdapter(searchPlaylistAdapter);
                }

                if (searchAlbumView.getVisibility() == View.GONE && searchPlaylistView.getVisibility() == View.GONE) {
                    showSearchError.setVisibility(View.VISIBLE);
                }
                hideDialog();
            }
        }
        searchSongs searchSongs = new searchSongs();
        searchSongs.execute();
    }

    private void SearchWithJsoup(final boolean hasmore, final String searchQuery) {

        progressBar.setVisibility(View.VISIBLE);
        final String[] url = {null};
        final boolean ISRUNNING0;
        final boolean ISRUNNING1;
        final boolean ISRUNNING2;

        /*
        if (position == 0) {
            url[0] = "https://www.jiosaavn.com/search/";
        } else if (position == 1) {
            url[0] = "https://www.jiosaavn.com/search/album/";

        } else if (position == 2) {
            url[0] = "https://www.jiosaavn.com/search/playlist/";
        } else {
            url[0] = "https://www.jiosaavn.com/search/";
        }
        */
        final String finalUrl = url[0];
        class searchWithJsoup extends AsyncTask<Void, Void, Boolean> {
            //String uri;

            /*
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (position == 0) {
                    uri = "https://www.jiosaavn.com/search/";
                } else if (position == 1) {
                    uri = "https://www.jiosaavn.com/search/album/";
                } else if (position == 2) {
                    uri = "https://www.jiosaavn.com/search/playlist/";
                } else {
                    uri = "https://www.jiosaavn.com/search/";
                }

            }*/

            @Override
            protected Boolean doInBackground(Void... voids) {

                String url = "https://www.jiosaavn.com/search/";

                Connection connection;
                if (hasmore) {
                    connection = (Connection) Jsoup.connect(baseLink + moreLink);
                } else {
                    connection = (Connection) Jsoup.connect(url + searchQuery);
                }

                try {
                    HttpConnection.Response response = (HttpConnection.Response) connection.execute();
                    cookies = response.cookies();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Document mainPage = null;
                try {
                    mainPage = connection.get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Elements songs = mainPage.select(".song-wrap");
                for (int i = 0; songs.size() > i; i++) {
                    Element jsonData = songs.select(".song-json").get(i);
                    String data = jsonData.text();
                    JSONObject object = null;
                    try {
                        object = new JSONObject(data);
                        jsonSongImage.add(object.getString("image_url"));
                        jsonSongEncUrl.add(object.getString("url"));
                        jsonSongSinger.add(object.getString("singers"));
                        jsonSongTitle.add(object.getString("title"));
                        jsonSongsID.add(object.getString("songid"));
                        jsonAlbumUrl.add(object.getString("album_url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                Element more = mainPage.select(".light").last();
                if (more.attributes().hasKey("href")) {
                    HASMORE = true;
                    moreLink = more.attributes().get("href");
                } else {
                    HASMORE = false;
                }


                return true;
            }

            @Override
            protected void onPostExecute(Boolean s) {
                super.onPostExecute(s);

                allsongs = findViewById(R.id.allsongs);

                JsonSongsAdapter jsonSongsAdapter = new JsonSongsAdapter(SearchUI.this, jsonSongTitle, jsonSongImage, jsonSongEncUrl, jsonSongSinger, jsonSongsID, jsonAlbumUrl);
                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
                allsongs.setLayoutManager(gridLayoutManager);
                allsongs.setAdapter(jsonSongsAdapter);
                progressBar.setVisibility(View.INVISIBLE);
                jsonSongsAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                    @Override
                    public void onBottomReached(int position) {
                        //your code goes here
                        if (HASMORE) {
                            SearchWithJsoup(true, null);
                        } else {
                            Toast.makeText(getApplicationContext(), "All songs loaded for the search query", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        }
        searchWithJsoup searchWithJsoup = new searchWithJsoup();
        searchWithJsoup.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.search_menu, menu);
        getMenuInflater().inflate(R.menu.searchui_menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setQueryHint("Ex. Emiway or Jump Kar");
        searchView.setIconified(false);
        searchView.setQuery(searchQuery, false);
        //hideKeyboard(getWindow().getDecorView());
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*songID.clear();
                songTitle.clear();
                songImage.clear();
                songDescription.clear();
                playlistID.clear();
                playlistTitle.clear();
                playlistImage.clear();
                albumID.clear();
                albumTitle.clear();
                albumImage.clear();
                albumMusic.clear();
                albumDescription.clear();
                searchQuery = query;
                StartSearching(); */
                jsonSongImage.clear();
                jsonSongEncUrl.clear();
                jsonSongSinger.clear();
                jsonSongTitle.clear();
                jsonSongsID.clear();
                jsonAlbumUrl.clear();
                //int position = viewHolder.getCurrentItem();
                searchQuery = query;
                SearchWithJsoup(false, searchQuery);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        songID.clear();
        songTitle.clear();
        songImage.clear();
        songDescription.clear();
        playlistID.clear();
        playlistTitle.clear();
        playlistImage.clear();
        albumID.clear();
        albumTitle.clear();
        albumImage.clear();
        albumMusic.clear();
        albumDescription.clear();
        jsonSongImage.clear();
        jsonSongEncUrl.clear();
        jsonSongSinger.clear();
        jsonSongTitle.clear();
        jsonSongsID.clear();
        jsonAlbumUrl.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.dismiss();
    }

    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch(Exception ignored) {
        }
    }

    public void handleSongClick(final String songid, final String albumurl, final ContentLoadingProgressBar jsonProgress) {

        currentSongHD.clear();
        currentSongName.clear();
        currentSongUrl.clear();

        class handlesongclick extends AsyncTask<Void, Void, String> {
            String id;

            @Override
            protected String doInBackground(Void... voids) {
                Connection connection = Jsoup.connect(albumurl);
                try {
                    Document document = connection.get();
                    Element albid = document.select(".play").first();
                    String a = albid.attributes().get("onclick");
                    Log.d("al", a);
                    id = a.substring(a.indexOf("['albumid','"), a.indexOf("'])")).replace("['albumid','", "");
                    Log.d("al", id);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Log.d("kbps", ALBUM_API + id);
                return new RequestHandler().sendPostRequest(ALBUM_API + id);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String kbps = "96";
                String url = null;
                boolean shouldStart = false;
                boolean linkGenFailed = false;
                try {
                    JSONObject object = new JSONObject(s.substring(s.indexOf("{")));
                    JSONArray songs = object.getJSONArray("songs");
                    for (int i = 0; songs.length() > i; i++) {
                        JSONObject jsonObject = songs.getJSONObject(i);
                        String sid = jsonObject.getString("id");
                        if (songid.equals(sid)) {
                            currentSongName.add(jsonObject.getString("song"));
                            currentSongHD.add(jsonObject.getBoolean("320kbps"));
                            if (jsonObject.getBoolean("320kbps")) {
                                kbps = "320";
                            }
                            if (jsonObject.has("media_preview_url")) {
                                url = jsonObject.getString("media_preview_url").replace("preview", "aac").replace("96_p", kbps);
                            } else {
                                linkGenFailed = true;
                                Snackbar.make(allsongs, "Link Generation Failed", Snackbar.LENGTH_SHORT).show();
                                break;
                            }

                            //https://preview.saavncdn.com/857/11fc4907553f050800a20440236497ce_96_p.mp4
                            currentSongUrl.add(url);
                            currentSong = jsonObject.getString("song");
                            currentAlbum = jsonObject.getString("album");
                            currentImage = jsonObject.getString("image");
                            currentSingers = jsonObject.getString("singers");
                            currentUrl = jsonObject.getString("media_preview_url");
                            currentYear = jsonObject.getString("year");
                            currentDuration = jsonObject.getInt("duration");
                            currentKBPS = jsonObject.getBoolean("320kbps");
                            currentCopyright = jsonObject.getString("copyright_text");
                            Log.d("kbps", String.valueOf(currentKBPS));
                            shouldStart = true;
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (shouldStart) {
                    Intent i = new Intent(getApplicationContext(), SongDetails.class);
                    i.putExtra("songName", currentSong);
                    i.putExtra("albumName", currentAlbum);
                    i.putExtra("imageUrl", currentImage);
                    i.putExtra("singerName", currentSingers);
                    i.putExtra("songUrl", currentUrl);
                    i.putExtra("songDate", currentYear);
                    i.putExtra("songDur", currentDuration);
                    i.putExtra("320", currentKBPS);
                    i.putExtra("copyright", currentCopyright);
                    Log.d("kbps", String.valueOf(currentKBPS));
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    jsonProgress.hide();

                }
                //MediaHandler mediaHandler = new MediaHandler(getApplicationContext());
               // mediaHandler.init(url);
            }
        }
        handlesongclick handlesongclick = new handlesongclick();
        handlesongclick.execute();

    }

    public void generateAuth(final String url) {

        String link = GENERATE_AUTH + "96&url=" + url;
        final String[] res = {""};

        /*
        OkHttpClient client = new OkHttpClient().newBuilder()

                .build();
        final Request request = new Request.Builder().url(link).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Toast.makeText(getApplicationContext(), "failed xt", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Toast.makeText(getApplicationContext(), "ahixi rh", Toast.LENGTH_LONG).show();
                //Reader inputStream = response.body().charStream();
                //response.toString();
                res[0] = response.body().string();
                Log.d("s", res[0]);
            }
        });
        */


        class getAuth extends AsyncTask<Void, Void, String> {
            String downUrl;
            String link = GENERATE_AUTH + "320&url=" + url;
            String res;

            @Override
            protected String doInBackground(Void... voids) {

                /*
                try {
                    Connection.Response response = Jsoup.connect("https://www.jiosaavn.com/").execute();
                    cookies = response.cookies();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
                Connection connection = Jsoup.connect(GENERATE_AUTH + "320&url=" + url).cookies(cookies);
                connection.header("method", "POST");
                connection.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
                try {
                    Document document = connection.get();
                    String json = document.text();
                    JSONObject object = new JSONObject(json);
                    downUrl = object.getString("auth_url");
                    Toast.makeText(getApplicationContext(), downUrl, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //String authHandler = new AuthHandler().sendPostRequest(GENERATE_AUTH + "96&url=" + url);
                //return new AuthHandler().sendPostRequest(GENERATE_AUTH + "96&url=" + url);

                /*
                OkHttpClient client = new OkHttpClient();
                final Request request = new Request.Builder().url(link).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getApplicationContext(), "failed xt", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Toast.makeText(getApplicationContext(), "ahixi rh", Toast.LENGTH_LONG).show();
                        response.toString();
                        res = response.toString();
                    }
                });
                */
                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                JSONObject object = null;
                try {
                    object = new JSONObject(s);
                    downUrl = object.getString("auth_url");
                    Toast.makeText(getApplicationContext(), downUrl, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        getAuth getAuth = new getAuth();
        getAuth.execute();
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
