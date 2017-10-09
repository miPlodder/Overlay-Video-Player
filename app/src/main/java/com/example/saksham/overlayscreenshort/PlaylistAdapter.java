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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by saksham on 10/9/2017.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    ArrayList<PlaylistPOJO> items;
    Context context;


    public PlaylistAdapter(Context context, ArrayList<PlaylistPOJO> items){

        this.context = context;
        this.items = items;

    }


    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_playlist_recycler_view,parent,false);


        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, int position) {

        holder.tvVideoName.setText(items.get(position).getName());
        holder.ivThumbnail.setImageBitmap(items.get(position).getThumbnail());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder{

        ImageView ivThumbnail;
        TextView tvVideoName;

        public PlaylistViewHolder(View itemView) {
            super(itemView);

            ivThumbnail = (ImageView) itemView.findViewById(R.id.ibVideoThumbnail);
            tvVideoName = (TextView) itemView.findViewById(R.id.tvVideoName);

        }
    }
}