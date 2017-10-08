package com.example.saksham.overlayscreenshort;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    Button btn;
    public static final String TAG = "MainActivity";
    public static final int GET_VIDEO_URL_CODE = 1221;
    public static final int OVERLAY_CODE = 1222;
    public static final int STORAGE_CODE = 34;
    ArrayList<Uri> videoList = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.askingStoragePermission();

        btn = (Button) findViewById(R.id.btn);

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

                //startActivityForResult(mediaChoser,GET_VIDEO_URL_CODE);
                startActivityForResult(Intent.createChooser(mediaChoser, "Select Videos"), GET_VIDEO_URL_CODE);

            }
        });

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

                if (data.getData() != null) {
                    Log.d(TAG, "data " + data.getData());
                } else {
                    //hack for mi, i guess to get external storage video link
                    ClipData mClipData = data.getClipData();

                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        videoList.add(uri);

                    }
                }
                Intent serviceIntent = new Intent(
                        MainActivity.this,
                        FloatService.class
                );
                serviceIntent.putExtra("videoList", videoList);
                startService(serviceIntent);
                break;
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
}
