package com.mridx.freemusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongSearchAdapter extends RecyclerView.Adapter<SongSearchAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> songID = new ArrayList<>();
    private ArrayList<String> songTitle = new ArrayList<>();
    private ArrayList<String> songImage = new ArrayList<>();
    private ArrayList<String> songMusic = new ArrayList<>();
    private ArrayList<String> songDescription = new ArrayList<>();

    public SongSearchAdapter(SearchUI activity, ArrayList<String> songID, ArrayList<String> songTitle, ArrayList<String> songImage, ArrayList<String> songDescription) {
        this.context = activity;
        this.songID = songID;
        this.songTitle = songTitle;
        this.songImage = songImage;
        this.songMusic = songMusic;
        this.songDescription = songDescription;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searchsongview, null);
        //View view = LayoutInflater.from(context).inflate(R.layout.search_result, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.searchSongNameView.setText(songTitle.get(position));
        holder.searchSongDescriptionView.setText(songDescription.get(position));
        Picasso.get().load(songImage.get(position)).into(holder.searchSongImageView);
    }

    @Override
    public int getItemCount() {
        return songID.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView searchSongNameView, searchSongDescriptionView;
        AppCompatImageView searchSongImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            searchSongDescriptionView = itemView.findViewById(R.id.jsonSongsDes);
            searchSongNameView = itemView.findViewById(R.id.jsonSongsName);
            searchSongImageView = itemView.findViewById(R.id.jsonSongsImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }
}
