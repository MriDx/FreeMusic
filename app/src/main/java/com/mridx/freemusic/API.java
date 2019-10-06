package com.mridx.freemusic;

import java.util.ArrayList;

public class API {


     public static final String SEARCH_API = "https://www.jiosaavn.com/api.php?_format=json&__call=autocomplete.get&query="; //song search api
     public static final String ALBUM_API = "https://www.jiosaavn.com/api.php?_format=json&__call=content.getAlbumDetails&albumid="; // album search api
     public static final String PLAYLIST_API = "https://www.jiosaavn.com/api.php?_format=json&__call=playlist.getDetails&listid="; // playlist api
     public static final String GENERATE_AUTH = "https://www.jiosaavn.com/api.php?__call=song.generateAuthToken&_marker=0&_format=json&birtate="; //get song auth


     public static ArrayList<String> AllSongsUri = new ArrayList<>();
     public static ArrayList<String> AllSongsName = new ArrayList<>();

     public static ArrayList<String> albumID = new ArrayList<>();
     public static ArrayList<String> albumTitle = new ArrayList<>();
     public static ArrayList<String> albumImage = new ArrayList<>();
     public static ArrayList<String> albumMusic = new ArrayList<>();
     public static ArrayList<String> albumDescription = new ArrayList<>();

     public static ArrayList<String> songID = new ArrayList<>();
     public static ArrayList<String> songTitle = new ArrayList<>();
     public static ArrayList<String> songImage = new ArrayList<>();
     public static ArrayList<String> songMusic = new ArrayList<>();
     public static ArrayList<String> songDescription = new ArrayList<>();

     public static ArrayList<String> playlistID = new ArrayList<>();
     public static ArrayList<String> playlistTitle = new ArrayList<>();
     public static ArrayList<String> playlistImage = new ArrayList<>();

     public static ArrayList<String> songsList = new ArrayList<>();
     public static ArrayList<String> songsImageList = new ArrayList<>();
     public static ArrayList<Boolean> songs320List = new ArrayList<>();
     public static ArrayList<String> songsUrlList = new ArrayList<>();

     public static ArrayList<String> jsonSongImage = new ArrayList<>();
     public static ArrayList<String> jsonSongEncUrl = new ArrayList<>();
     public static ArrayList<String> jsonSongTitle = new ArrayList<>();
     public static ArrayList<String> jsonSongSinger = new ArrayList<>();
     public static ArrayList<String> jsonSongsID = new ArrayList<>();
     public static ArrayList<String> jsonAlbumUrl = new ArrayList<>();

     public static ArrayList<String> currentSongName = new ArrayList<>();
     public static ArrayList<String> currentSongUrl = new ArrayList<>();
     public static ArrayList<Boolean> currentSongHD = new ArrayList<>();

     public static ArrayList<String> song = new ArrayList<>();
     public static ArrayList<String> artist = new ArrayList<>();
     public static ArrayList<byte[]> albumart = new ArrayList<byte[]>();


}
