package com.example.saksham.overlayscreenshort;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
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
    WindowManager.LayoutParams prevParams;
    int videoPosition = 0;
    GestureDetector gestureDetector;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public FloatService() {

    }

    public void initialise(Intent intent) {

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_service_overlay, null);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll);
        vvVideo = (VideoView) view.findViewById(R.id.vvVideo);
        ibtnClose = (ImageButton) view.findViewById(R.id.ibtnClose);

        //initilaising the shared preference
        sharedPreferences = getSharedPreferences(Constants.COMMON_SHARED_PREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //storing 0 temporary in the shared preference
        editor.putInt(Constants.CURRENT_VIDEO_SHARED_PREF, 0);
        editor.commit();

        videoList = (ArrayList<Uri>) intent.getSerializableExtra("videoList");
        videoPosition = intent.getIntExtra("position", -1);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        ibtnClose.setOnClickListener(this);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {

                openActivity();
                return super.onDoubleTap(e);
            }

        });
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

        if (videoList.size() != 0) {

            vvVideo.setVideoURI(videoList.get(videoPosition));
            vvVideo.requestFocus();
            vvVideo.start();
            vvVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    prevVideoIndex++;
                    int temp = ((prevVideoIndex) % videoList.size());
                    vvVideo.setVideoURI(videoList.get(temp));

                    editor.putInt(Constants.CURRENT_VIDEO_SHARED_PREF, temp);
                    editor.commit();
                    vvVideo.start();
                }
            });
        } else {

            stopSelf();
            Toast.makeText(this, "No video in Playlist", Toast.LENGTH_SHORT).show();
        }
    }

    public void addWindowManager() {

        wParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        wParams.x = sharedPreferences.getInt(Constants.CURRENT_X, -1);
        wParams.y = sharedPreferences.getInt(Constants.CURRENT_Y, -1);

        if (!isFirstView) {

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
                        prevParams = updatedParams;

                        editor.putInt(Constants.CURRENT_X, updatedParams.x);
                        editor.putInt(Constants.CURRENT_Y, updatedParams.y);
                        editor.commit();

                        break;

                    default:
                        break;

                }

                gestureDetector.onTouchEvent(event);

                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //closing the service on clicking "X"
            case R.id.ibtnClose:

                stopSelf();
                break;

            case R.id.ll:

                openActivity();
                break;
        }

    }

    public void openActivity() {

        startActivity((new Intent(
                this,
                MainActivity.class
        )).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: inside service");
        windowManager.removeView(view);
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
