package com.example.testbubble;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class TestDemo extends Service {

    private LayoutInflater li;
    protected ImageView imageView;
    protected AnimationDrawable anim;
    protected View view;
    protected WindowManager wm;

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        view = LayoutInflater.from(this).inflate(R.layout.test, null);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int deviceWidth = size.x;
        int deviceHeight = size.y;
        imageView = (ImageView) view.findViewById(R.id.hello);
        if(view==null) throw new AssertionError();
        imageView.setBackgroundResource(R.drawable.walk);
        anim= (AnimationDrawable) imageView.getBackground();
        anim.start();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;

        params.x = 0;
        params.y = (int) (deviceHeight / 2);
        // Thêm view vào màn hình
        wm.addView(view, params);

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
