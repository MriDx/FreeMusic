package com.mridx.freemusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlbumSearchAdapter extends RecyclerView.Adapter<AlbumSearchAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> albumID = new ArrayList<>();
    private ArrayList<String> albumTitle = new ArrayList<>();
    private ArrayList<String> albumImage = new ArrayList<>();
    private ArrayList<String> albumMusic = new ArrayList<>();
    private ArrayList<String> albumDescription = new ArrayList<>();

    public AlbumSearchAdapter(SearchUI activity, ArrayList<String> albumID, ArrayList<String> albumTitle, ArrayList<String> albumImage, ArrayList<String> albumDescription) {
        this.context = activity;
        this.albumID = albumID;
        this.albumTitle = albumTitle;
        this.albumImage = albumImage;
        //this.albumMusic = albumMusic;
        this.albumDescription = albumDescription;
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searchalbumview, null);
        //View view = LayoutInflater.from(context).inflate(R.layout.search_result, null);
        return new AlbumSearchAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.searchAlbumNameView.setText(albumTitle.get(position));
        holder.searchAlbumDescriptionView.setText(albumDescription.get(position));
        Picasso.get().load(albumImage.get(position)).into(holder.searchAlbumImageView);
    }

    @Override
    public int getItemCount() {
        return albumID.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView searchAlbumNameView, searchAlbumDescriptionView;
        AppCompatImageView searchAlbumImageView;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            searchAlbumDescriptionView = itemView.findViewById(R.id.searchAlbumDescriptionView);
            searchAlbumNameView = itemView.findViewById(R.id.searchAlbumNameView);
            searchAlbumImageView = itemView.findViewById(R.id.searchAlbumImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, SongsList.class);
                    i.putExtra("albumID", albumID.get(getAdapterPosition()));
                    ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    itemView.getContext().startActivity(i);
                }
            });
        }
    }
}
