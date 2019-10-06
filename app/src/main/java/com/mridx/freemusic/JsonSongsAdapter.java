package com.mridx.freemusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class JsonSongsAdapter extends RecyclerView.Adapter<JsonSongsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> jsonSongTitle = new ArrayList<>();
    private ArrayList<String> jsonSongImage = new ArrayList<>();
    private ArrayList<String> jsonSongEncUrl = new ArrayList<>();
    private ArrayList<String> jsonSongSinger = new ArrayList<>();
    private ArrayList<String> jsonSongID = new ArrayList<>();
    private ArrayList<String> jsonAlbumUrl = new ArrayList<>();

    SearchUI.OnBottomReachedListener onBottomReachedListener;

    public JsonSongsAdapter(SearchUI activity, ArrayList<String> jsonSongTitle, ArrayList<String> jsonSongImage, ArrayList<String> jsonSongEncUrl,
                            ArrayList<String> jsonSongSinger, ArrayList<String> jsonSongID, ArrayList<String> jsonAlbumUrl) {
        this.context = activity;
        this.jsonSongTitle = jsonSongTitle;
        this.jsonSongImage = jsonSongImage;
        this.jsonSongEncUrl = jsonSongEncUrl;
        this.jsonSongSinger = jsonSongSinger;
        this.jsonSongID = jsonSongID;
        this.jsonAlbumUrl = jsonAlbumUrl;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.json_songssearch, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.SongName.setText(jsonSongTitle.get(position));
        Picasso.get().load(jsonSongImage.get(position)).into(holder.SongImage);
        holder.SongDesc.setText(jsonSongSinger.get(position));
        if (position == jsonSongTitle.size() - 1){

            onBottomReachedListener.onBottomReached(position);

        }
    }

    @Override
    public int getItemCount() {
        return jsonSongTitle.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView SongImage;
        AppCompatTextView SongName, SongDesc;
        ContentLoadingProgressBar jsonProgress;
        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            SongImage = itemView.findViewById(R.id.jsonSongsImage);
            SongName = itemView.findViewById(R.id.jsonSongsName);
            SongDesc = itemView.findViewById(R.id.jsonSongsDes);
            jsonProgress = itemView.findViewById(R.id.jsonProgress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jsonProgress.setVisibility(View.VISIBLE);
                    final String songid = jsonSongID.get(getAdapterPosition());
                    final String albumurl = jsonAlbumUrl.get(getAdapterPosition());
                    if (context instanceof SearchUI) {
                        ((SearchUI)context).handleSongClick(songid, albumurl, jsonProgress);
                    }
                }
            });

        }
    }

    public void setOnBottomReachedListener(SearchUI.OnBottomReachedListener onBottomReachedListener){

        this.onBottomReachedListener = onBottomReachedListener;
    }

}
