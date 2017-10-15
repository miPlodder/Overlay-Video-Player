package com.example.saksham.overlayscreenshort;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by saksham on 10/9/2017.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    int previousPosition;
    ArrayList<PlaylistPOJO> items;
    Context context;
    ArrayList<Uri> videoUri;
    OnItemClickListener onItemClickListener;
    OnStartNewService onStartNewService;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static ArrayList<PlaylistViewHolder> holderList;

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
        sharedPreferences = context.getSharedPreferences(Constants.COMMON_SHARED_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        holderList = new ArrayList<>();

    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_playlist_recycler_view, parent, false);

        PlaylistViewHolder holder = new PlaylistViewHolder(view);
        holderList.add(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, final int position) {

        if (sharedPreferences.getInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, -1) == 0) {

            PlaylistAdapter.changeActiveItemBackground(0);
        }

        holder.tvVideoName.setText(items.get(position).getName());

        if(items.get(position).getThumbnail() == null){

            holder.ivThumbnail.setImageResource(R.drawable.inf);

        }else{

            holder.ivThumbnail.setImageBitmap(items.get(position).getThumbnail());
        }

        holder.tvHolderId.setText("position =" + position);

        holder.ibRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == videoUri.size()) {

                }

                //holderList.remove(position); //no need to remove element from recycler view
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
        TextView tvVideoName, tvHolderId;
        ImageButton ibRemove;
        LinearLayout llClicker, llItem;

        public PlaylistViewHolder(View itemView) {
            super(itemView);

            ivThumbnail = (ImageView) itemView.findViewById(R.id.ibVideoThumbnail);
            tvVideoName = (TextView) itemView.findViewById(R.id.tvVideoName);
            ibRemove = (ImageButton) itemView.findViewById(R.id.ibRemove);
            llClicker = (LinearLayout) itemView.findViewById(R.id.llClicker);
            llItem = (LinearLayout) itemView.findViewById(R.id.llItem);
            tvHolderId = (TextView) itemView.findViewById(R.id.tvHolderId);

        }
    }

    private ArrayList<Uri> setVideoUri() {

        videoUri.clear();

        for (PlaylistPOJO item : items) {

            videoUri.add(item.getUri());

        }

        return videoUri;
    }

    public static void changeActiveItemBackground(int currPosition) {




        /*if (prevPosition != -1) {
            holderList.get(prevPosition).llItem.setBackgroundColor(Color.WHITE);
        }
        */
        for(int i = 0 ; i < holderList.size() ; i++){

            holderList.get(i).llItem.setBackgroundColor(Color.WHITE);

        }

        if (holderList.size() > currPosition && currPosition != -1) {
            holderList.get(currPosition).llItem.setBackgroundColor(Color.argb(150, 100, 100, 100));
            Log.d("HAHA", "Holder Size " + currPosition);
        }

    }
}