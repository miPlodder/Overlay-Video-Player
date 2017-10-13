package com.example.saksham.overlayscreenshort;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by saksham on 10/9/2017.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    ArrayList<PlaylistPOJO> items;
    Context context;
    ArrayList<Uri> videoUri;
    OnItemClickListener onItemClickListener;
    OnStartNewService onStartNewService;

    interface OnItemClickListener {

        void setOnItemClickListener(ArrayList<Uri> videoUri, int position);

    }

    interface OnStartNewService {

        void onStartService(ArrayList<Uri> videoUri, int position);
    }


    public PlaylistAdapter(Context context, ArrayList<PlaylistPOJO> items, ArrayList<Uri> videoUri, OnItemClickListener onItemClickListener, OnStartNewService onStartNewService) {

        this.context = context;
        this.items = items;
        this.videoUri = videoUri;
        this.onStartNewService = onStartNewService;
        this.onItemClickListener = onItemClickListener;

    }


    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_playlist_recycler_view, parent, false);


        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, final int position) {

        holder.tvVideoName.setText(items.get(position).getName());
        holder.ivThumbnail.setImageBitmap(items.get(position).getThumbnail());

        holder.ibRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                items.remove(position);
                onStartNewService.onStartService(setVideoUri(), position);
                notifyDataSetChanged();
            }
        });

        holder.llClicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onItemClickListener.setOnItemClickListener(setVideoUri(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {

        ImageView ivThumbnail;
        TextView tvVideoName;
        ImageButton ibRemove;
        LinearLayout llClicker;

        public PlaylistViewHolder(View itemView) {
            super(itemView);

            ivThumbnail = (ImageView) itemView.findViewById(R.id.ibVideoThumbnail);
            tvVideoName = (TextView) itemView.findViewById(R.id.tvVideoName);
            ibRemove = (ImageButton) itemView.findViewById(R.id.ibRemove);
            llClicker = (LinearLayout) itemView.findViewById(R.id.llClicker);

        }
    }

    private ArrayList<Uri> setVideoUri() {

        videoUri.clear();

        for (PlaylistPOJO item : items) {

            videoUri.add(item.getUri());

        }

        return videoUri;
    }
}