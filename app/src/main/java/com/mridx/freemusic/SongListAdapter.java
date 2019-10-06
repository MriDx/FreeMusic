package com.mridx.freemusic;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder> {

    private ArrayList<String> songsList = new ArrayList<>();
    private ArrayList<String> songsImageList = new ArrayList<>();
    private ArrayList<Boolean> songs320List = new ArrayList<>();
    private ArrayList<String> songsUrlList = new ArrayList<>();
    private Context context;

    public SongListAdapter(SongsList activity, ArrayList<String> songsList, ArrayList<String> songsImageList, ArrayList<Boolean> songs320List, ArrayList<String> songsUrlList) {
        this.context = activity;
        this.songsList = songsList;
        this.songsImageList = songsImageList;
        this.songs320List = songs320List;
        this.songsUrlList = songsUrlList;
    }

    @NonNull
    @Override
    public SongListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searchalbumview, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListAdapter.MyViewHolder holder, int position) {
        holder.songName.setText(songsList.get(position));
        Picasso.get().load(songsImageList.get(position)).into(holder.songImage);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView songName;
        AppCompatImageView songImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.searchAlbumNameView);
            songImage = itemView.findViewById(R.id.searchAlbumImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (context instanceof SongsList) {
                        ((SongsList)context).startPlaying(songsUrlList.get(getAdapterPosition()), songsList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
