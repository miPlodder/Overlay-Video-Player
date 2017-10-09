package com.example.saksham.overlayscreenshort;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    Button btn;
    public static final String TAG = "MainActivity";
    public static final int GET_VIDEO_URL_CODE = 1221;
    public static final int OVERLAY_CODE = 1222;
    public static final int STORAGE_CODE = 34;
    ArrayList<Uri> videoList;
    Intent serviceIntent;
    RecyclerView rvPlaylist;
    ArrayList<PlaylistPOJO> playlist;
    PlaylistAdapter playlistAdapter;
    TextView tvTemp;

    public void initialise() {

        btn = (Button) findViewById(R.id.btn);
        rvPlaylist = (RecyclerView) findViewById(R.id.rvPlaylist);
        tvTemp = (TextView) findViewById(R.id.tvTemp);

        videoList = new ArrayList<>();

        playlist = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(this, playlist);
        rvPlaylist.setAdapter(playlistAdapter);
        rvPlaylist.setLayoutManager(new LinearLayoutManager(this));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                askingPermissionForOverlayScreen();

                Intent mediaChoser = new Intent();
                mediaChoser.setAction(Intent.ACTION_GET_CONTENT);
                mediaChoser.setType("video/*");

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
                    mediaChoser.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                else
                    Toast.makeText(MainActivity.this, "ELSE", Toast.LENGTH_SHORT).show();

                startActivityForResult(Intent.createChooser(mediaChoser, "Select Videos"), GET_VIDEO_URL_CODE);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initialise();
        this.askingStoragePermission();

    }

    public void askingPermissionForOverlayScreen() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(myIntent, OVERLAY_CODE);
            }
        }
    }

    public void askingStoragePermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //permission granted do nothing
        } else {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_CODE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case OVERLAY_CODE:

                Toast.makeText(this, "Overlay Activity Result", Toast.LENGTH_SHORT).show();
                break;

            case GET_VIDEO_URL_CODE:

                if (data != null) {
                    if (data.getData() != null) {
                        Log.d(TAG, "data " + data.getData());
                    } else {
                        //hack for mi, i guess to get external storage video link
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            videoList.add(uri);
                            playlist.add(new PlaylistPOJO(uri.toString(), this.createVideoThumbnail(uri)));

                        }
                    }

                    Log.d(TAG, "onActivityResult: " + playlist.toString());
                    tvTemp.setVisibility(View.GONE);
                    rvPlaylist.setVisibility(View.VISIBLE);

                    playlistAdapter.notifyDataSetChanged();

                    if (serviceIntent != null) {

                        stopService(serviceIntent);
                    }
                    serviceIntent = new Intent(
                            MainActivity.this,
                            FloatService.class
                    );
                    serviceIntent.putExtra("videoList", videoList);
                    startService(serviceIntent);
                    break;
                }

                Log.d(TAG, "playlist " + playlist);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case STORAGE_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                }

        }

    }

    public Bitmap createVideoThumbnail(Uri uri) {

        File path = new File(uri.toString().substring(5));

        Log.d(TAG, "createVideoThumbnail: "+path.getAbsolutePath());

        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(path.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

        Toast.makeText(this, "" + thumbnail, Toast.LENGTH_SHORT).show();
        return thumbnail;
    }
}
