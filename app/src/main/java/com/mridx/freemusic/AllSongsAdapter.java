package com.mridx.freemusic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static com.mridx.freemusic.MainUI.adapterPostion;
import static com.mridx.freemusic.MainUI.next;
import static com.mridx.freemusic.API.AllSongsName;
import static com.mridx.freemusic.API.AllSongsUri;

public class AllSongsAdapter extends RecyclerView.Adapter<AllSongsAdapter.MyViewHolder> {

    private ArrayList<String> AllSongsUri;
    private ArrayList<String> AllSongsName;
    private ArrayList<String> artist;
    private ArrayList<byte[]> albumArt;

    private Context context;
    private Context xt;

    public AllSongsAdapter(MainUI activity, ArrayList<String> AllSongsUri, ArrayList<String> AllSongsName, ArrayList<String> artist, ArrayList<byte[]> albumart) {
        this.context = activity;
        this.AllSongsUri = AllSongsUri;
        this.AllSongsName = AllSongsName;
        this.artist = artist;
        this.albumArt = albumart;

    }

    public AllSongsAdapter(MainUI context) {
        xt = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_view, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.songView.setText(AllSongsName.get(position));
        holder.artistView.setText(artist.get(position));
        byte[] m = albumArt.get(position);
        if (m != null) {
            Bitmap songImage = BitmapFactory.decodeByteArray(albumArt.get(position), 0, albumArt.get(position).length);
            holder.artView.setImageBitmap(songImage);
        }

    }

    @Override
    public int getItemCount() {
        return AllSongsName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView songView, artistView;
        AppCompatImageView artView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songView = itemView.findViewById(R.id.songView);
            artistView = itemView.findViewById(R.id.artistView);
            artView = itemView.findViewById(R.id.artView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "Clicked on Song", Toast.LENGTH_SHORT).show();
                    String uri = AllSongsUri.get(getAdapterPosition());
                    String name = AllSongsName.get(getAdapterPosition());
                    int position = getAdapterPosition();
                    int all = AllSongsUri.size();
                    ((MainUI)context).PlayPause(uri, name, position);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //Toast.makeText(v.getContext(), "Long Pressed on song", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

        }
    }

    public void getNext() {
        if (API.AllSongsName.size() != 0) {
            int nextPosition = adapterPostion + 1;
            int all = API.AllSongsUri.size() -1;
            if (all < nextPosition) {
                nextPosition = 0;
            }
            String uri = API.AllSongsUri.get(nextPosition);
            String name = API.AllSongsName.get(nextPosition);
            if (xt instanceof MainUI) {
                ((MainUI) xt).PlayPause(uri, name, nextPosition);
            }
        } else {
            Toast.makeText(xt, "No Song Found", Toast.LENGTH_SHORT).show();
        }


    }

    public void getPrev() {
        if (API.AllSongsUri.size() != 0) {
            int prevPosition = adapterPostion - 1;
            int all = API.AllSongsUri.size() - 1;
            if (prevPosition < 0) {
                prevPosition = all;
            }
            String uri = API.AllSongsUri.get(prevPosition);
            String name = API.AllSongsName.get(prevPosition);
            if (xt instanceof MainUI) {
                ((MainUI) xt).PlayPause(uri, name, prevPosition);
            }
        } else {
            Toast.makeText(xt, "No Song Found", Toast.LENGTH_SHORT).show();
        }


    }

}
