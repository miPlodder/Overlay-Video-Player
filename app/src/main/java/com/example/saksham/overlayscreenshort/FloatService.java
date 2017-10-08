package com.example.saksham.overlayscreenshort;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class FloatService extends Service implements View.OnClickListener {

    WindowManager windowManager;
    LinearLayout linearLayout;
    ImageButton ibtnClose;
    public static final String TAG = "FloatService";
    LayoutInflater inflater;
    VideoView vvVideo;
    ArrayList<Uri> videoList;
    View view;
    int prevVideoIndex = 0;
    WindowManager.LayoutParams wParams;
    boolean isFirstView = true;

    public FloatService() {

    }

    public void initialise(Intent intent) {

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_service_overlay, null);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll);
        vvVideo = (VideoView) view.findViewById(R.id.vvVideo);
        ibtnClose = (ImageButton) view.findViewById(R.id.ibtnClose);

        videoList = (ArrayList<Uri>) intent.getSerializableExtra("videoList");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        ibtnClose.setOnClickListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        this.initialise(intent);
        this.addVideoToVideoView();
        this.addWindowManager();
        //String stringURI = "android.resource://com.example.saksham.overlayscreenshort/" + R.raw.video;

        return START_STICKY;
    }

    public void addVideoToVideoView() {

        vvVideo.setVideoURI(videoList.get(0));
        vvVideo.requestFocus();
        vvVideo.start();
        vvVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                prevVideoIndex++;
                int temp = ((prevVideoIndex) % videoList.size());
                vvVideo.setVideoURI(videoList.get(temp));
                vvVideo.start();
            }
        });
    }

    public void addWindowManager() {

        wParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        wParams.x = 0;
        wParams.y = 0;
        wParams.gravity = Gravity.CENTER;

        if (!isFirstView) {
            Toast.makeText(this, "removing", Toast.LENGTH_SHORT).show();
            isFirstView = false;
            windowManager.removeView(view);
        }
        windowManager.addView(view, wParams);

        view.setOnTouchListener(new View.OnTouchListener() {

            WindowManager.LayoutParams updatedParams = wParams;
            int x, y;
            float touchedX, touchedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    //motion has started
                    case MotionEvent.ACTION_DOWN:

                        x = updatedParams.x;
                        y = updatedParams.y;

                        touchedX = event.getRawX();
                        touchedY = event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:

                        updatedParams.x = (int) (x + (event.getRawX() - touchedX));
                        updatedParams.y = (int) (y + (event.getRawY()) - touchedY);

                        windowManager.updateViewLayout(linearLayout, updatedParams);

                        break;
                    default:
                        break;
                }

                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //closing the service on clicking "X"
            case R.id.ibtnClose:

                windowManager.removeView(linearLayout);
                stopSelf();
                break;
        }

    }


    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
