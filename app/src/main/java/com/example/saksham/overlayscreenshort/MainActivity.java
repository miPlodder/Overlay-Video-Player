package com.example.saksham.overlayscreenshort;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int GET_VIDEO_URL_CODE = 1221;
    public static final int OVERLAY_CODE = 1222;
    public static final int STORAGE_CODE = 34;
    Intent serviceIntent;
    RecyclerView rvPlaylist;
    ArrayList<PlaylistPOJO> playlist;
    ArrayList<Uri> videoUri;
    PlaylistAdapter playlistAdapter;
    TextView tvTemp;
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;
    int currentVideoPosition;
    LinearLayoutManager linearLayoutManager;


    public void initialise() {

        getSupportActionBar().setTitle("Playlist");

        rvPlaylist = (RecyclerView) findViewById(R.id.rvPlaylist);

        tvTemp = (TextView) findViewById(R.id.tvTemp);

        videoUri = new ArrayList<>();

        //initialising the shared preference
        sharedPreference = getSharedPreferences(Constants.COMMON_SHARED_PREF, MODE_PRIVATE);
        editor = sharedPreference.edit();

        //initialising the shared prefereence variable
        editor.putInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, 0);
        editor.putInt(Constants.CURRENT_X, 0);
        editor.putInt(Constants.CURRENT_Y, 0);

        editor.commit();

        playlist = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(this, playlist, videoUri,
                new PlaylistAdapter.OnItemClickListener() {
                    @Override
                    public void setOnItemClickListener(ArrayList<Uri> videoUri, int position) {

                        //changing the background color
                        PlaylistAdapter.changeActiveItemBackground(position);

                        editor.putInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, position);
                        editor.commit();

                        startNewService(videoUri, position, false);

                    }
                },

                //close
                new PlaylistAdapter.OnStartNewService() {

                    @Override
                    public void onStartService(ArrayList<Uri> videoUri, int position) {

                        currentVideoPosition = sharedPreference.getInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, -1);

                        //position and playing video are same
                        if (position == currentVideoPosition) {

                            //from here service will be started when item from recycler view is deleted
                            if (position == (videoUri.size())) {

                                editor.putInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, 0);
                                editor.commit();

                                startNewService(videoUri, 0, false);
                                PlaylistAdapter.changeActiveItemBackground(0);

                            } else {

                                startNewService(videoUri, position, false);
                                PlaylistAdapter.changeActiveItemBackground(position);
                            }

                            //pending
                        } else if (position < currentVideoPosition) {

                            editor.putInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, currentVideoPosition - 1);
                            editor.commit();

                            //which video to play
                            startNewService(videoUri, currentVideoPosition - 1, false);
                            PlaylistAdapter.changeActiveItemBackground(currentVideoPosition - 1);

                        } else if (position > currentVideoPosition) {

                            startNewService(videoUri, currentVideoPosition, false);
                            PlaylistAdapter.changeActiveItemBackground(currentVideoPosition);
                        }
                    }
                });

        rvPlaylist.setAdapter(playlistAdapter);

        linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.scrollToPosition(videoUri.size() - 1);
        //linearLayoutManager.smoothScrollToPosition(rvPlaylist, null, videoUri.size() - 1);
        //rvPlaylist.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        //rvPlaylist.setLayoutManager(new GridLayoutManager(this, 2));
        rvPlaylist.setLayoutManager(linearLayoutManager);
        rvPlaylist.setItemViewCacheSize(20);
        rvPlaylist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                PlaylistAdapter.changeActiveItemBackground(sharedPreference.getInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, -1));
            }
        });
        //rvPlaylist.setNestedScrollingEnabled(true);
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
                            if (isItemPresent(uri)) {
                                //do nothing
                            } else {
                                videoUri.add(uri);
                                playlist.add(new PlaylistPOJO(this.pathToNameConvertor(uri.toString()),
                                        this.createVideoThumbnail(uri), uri));
                            }
                        }
                    }



                    playlistAdapter.notifyDataSetChanged();

                    //from here service will be called when videos are selected from the gallery
                    this.startNewService(videoUri, sharedPreference.getInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, -1), true);
                    break;
                }
        }
    }

    //position - the video which has to be played from the playlist
    //isFromIntent -> true when from intent
    //false when from onclick listener
    public void startNewService(ArrayList<Uri> videoUri, int position, boolean isFromIntent) {

        if (videoUri.size() == 0) {

            tvTemp.setVisibility(View.VISIBLE);
            rvPlaylist.setVisibility(View.GONE);

        } else {

            tvTemp.setVisibility(View.GONE);
            rvPlaylist.setVisibility(View.VISIBLE);
        }

        if (serviceIntent != null) {

            stopService(serviceIntent);
        }
        serviceIntent = new Intent(
                MainActivity.this,
                FloatService.class
        );

        serviceIntent.putExtra("videoList", videoUri);

        if (isFromIntent) {
            //send from preferncec //no use of position in this case
            serviceIntent.putExtra("position", sharedPreference.getInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, -1));
        } else {

            Log.d(TAG, "startNewService: from click -> position = " + position + ", sharedpref = " + sharedPreference.getInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, -1));
            serviceIntent.putExtra("position", position);
        }

        startService(serviceIntent);

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

        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(path.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

        return thumbnail;
    }

    public String pathToNameConvertor(String path) {

        StringBuffer videoName = new StringBuffer();

        Log.d(TAG, "pathToNameConvertor: path -> " + path);
        for (int i = path.length() - 1; i >= 0; i--) {

            if (path.charAt(i) == '/') {
                break;
            }

            videoName.append(path.charAt(i));
        }

        String rv = videoName.reverse().toString();
        rv = rv.replaceAll("%5B", "[");
        rv = rv.replaceAll("%5D", "]");
        rv = rv.replaceAll("%20", " ");
        rv = rv.replace(".mp4","");

        return rv;
    }

    public boolean isItemPresent(Uri uri) {

        for (Uri item : videoUri) {

            if (item.toString().equals(uri.toString())) {
                return true;
            } else {

            }
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menuAdd:

                askingPermissionForOverlayScreen();

                Intent mediaChoser = new Intent();
                mediaChoser.setAction(Intent.ACTION_GET_CONTENT);
                mediaChoser.setType("video/*");

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2)
                    mediaChoser.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                else {
                }

                startActivityForResult(Intent.createChooser(mediaChoser, "Select Videos"), GET_VIDEO_URL_CODE);


                break;

            case R.id.menuRemove:

                clearPlaylist();
                break;


            case R.id.menuHelp:

                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public void clearPlaylist() {

        //shared pref, videoUri, playlist pojo clean
        editor.putInt(Constants.CURRENT_PLAYING_VIDEO_NUMBER, 0);
        editor.commit();

        videoUri.clear();
        startNewService(videoUri, 0, false);

        playlist.clear();
        playlistAdapter.notifyDataSetChanged();

    }
}
