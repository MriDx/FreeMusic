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

public class SearchPlaylistAdapter extends RecyclerView.Adapter<SearchPlaylistAdapter.MyViewHolder> {

    private ArrayList<String> playlistID = new ArrayList<>();
    private ArrayList<String> playlistTitle = new ArrayList<>();
    private ArrayList<String> playlistImage = new ArrayList<>();
    private Context context;

    public SearchPlaylistAdapter(SearchUI activity, ArrayList<String> playlistID, ArrayList<String> playlistTitle, ArrayList<String> playlistImage) {
        this.context = activity;
        this.playlistID = playlistID;
        this.playlistImage = playlistImage;
        this.playlistTitle = playlistTitle;
    }

    @NonNull
    @Override
    public SearchPlaylistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searchalbumview, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchPlaylistAdapter.MyViewHolder holder, int position) {
        holder.searchAlbumNameView.setText(playlistTitle.get(position));
        Picasso.get().load(playlistImage.get(position)).into(holder.searchAlbumImageView);
    }

    @Override
    public int getItemCount() {
        return playlistID.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView searchAlbumNameView, searchAlbumDescriptionView;
        AppCompatImageView searchAlbumImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            searchAlbumDescriptionView = itemView.findViewById(R.id.searchAlbumDescriptionView);
            searchAlbumNameView = itemView.findViewById(R.id.searchAlbumNameView);
            searchAlbumImageView = itemView.findViewById(R.id.searchAlbumImageView);
        }
    }
}
