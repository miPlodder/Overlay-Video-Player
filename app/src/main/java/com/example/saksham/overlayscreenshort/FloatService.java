package com.example.saksham.overlayscreenshort;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.VideoView;

public class FloatService extends Service {

    WindowManager windowManager;
    LinearLayout linearLayout;
    ImageButton ibtnClose;
    public static final String TAG = "FloatService";
    LayoutInflater inflator;
    VideoView vvVideo;
    Uri videoURI;

    public FloatService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        inflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = inflator.inflate(R.layout.layout_service_overlay, null);
        view.setBackgroundColor(Color.argb(100, 0, 0, 0));

        linearLayout = (LinearLayout) view.findViewById(R.id.ll);
        ibtnClose = (ImageButton) view.findViewById(R.id.ibtnClose);
        vvVideo = (VideoView) view.findViewById(R.id.vvVideo);
        vvVideo.setVisibility(View.VISIBLE);

        //String stringURI = "android.resource://com.example.saksham.overlayscreenshort/" + R.raw.video;

        String stringURI = intent.getStringExtra("videoURI");
        Log.d(TAG, "onStartCommand: " + stringURI);
        Uri uri = Uri.parse(stringURI);
        vvVideo.setVideoURI(uri);
        vvVideo.requestFocus();
        vvVideo.start();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams wParams = new WindowManager.LayoutParams(600, 400, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        wParams.x = 0;
        wParams.y = 0;
        wParams.gravity = Gravity.CENTER;
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

        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                windowManager.removeView(linearLayout);
                stopSelf();
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
