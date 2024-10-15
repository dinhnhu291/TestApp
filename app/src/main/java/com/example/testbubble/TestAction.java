package com.example.testbubble;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Random;

public class TestAction extends Service {

    protected WindowManager wm;
    private FrameLayout Layout;
    private LayoutInflater li;
    private View myview;
    protected ImageView view;
    protected   WindowManager.LayoutParams params;
    public int width, height;
    private NotificationManager nm;
    private MediaPlayer mp,sp,bp;
    private CountDownTimer touched;
    private int Touch=0;
    private long timu=5000;
    private int timer;
    private boolean sticky= false, over=false, under=false;
    private CountDownTimer running, timing, moving;
    private int sens=1;
    private boolean isPaused = true;
    private boolean isMuted = false;
    Handler handler = new Handler(Looper.getMainLooper());
    TextView textView;
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    boolean isFlipped = false;
    AnimationDrawable walk_an, blink_an;
    Animator walk_fl;
    private String currentNotify;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (wm==null) {
            ShimejiView();
            randomsens();
            onReceiveNotify("Hello, thời tiết hôm nay 30 độ");
            handler.post(draw());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myview != null) {
            wm.removeView(view);
            wm = null;
        }
    }
    private BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopServiceActions();
        }
    };
    public void stopServiceActions() {
        // Dừng các hoạt động của dịch vụ tại đây
        if (view != null) {
            walk_an.stop();
            wm.removeView(view);
            myview = null;
        }
        // Giải phóng các tài nguyên khác nếu cần
    }
    private Runnable stopDrawRunnable;
//Action
private class action implements View.OnTouchListener {
    private int lastAction;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                stopDraw();
                walk_an.stop();
                view.setBackgroundResource(R.drawable.pullup);
                blink_an = (AnimationDrawable)view.getBackground();
                blink_an.start();
                //lấy vị trí
                initialX = params.x;
                initialY = params.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                lastAction = event.getAction();
//                return gestureDetector.onTouchEvent(event);
//                handler.postDelayed(draw(),10000);
                return true;
            case MotionEvent.ACTION_MOVE:
                stopDraw();
                blink_an.stop();
//                view=(ImageView)myview.findViewById(R.id.remove);
                view.setBackgroundResource(R.drawable.falling);
                blink_an = (AnimationDrawable)view.getBackground();
                blink_an.start();
                int xDiff = (int) (event.getRawX() - initialTouchX);
                int yDiff = (int) (event.getRawY() - initialTouchY);

                params.x = initialX + xDiff;
                params.y = initialY + yDiff;

                if (params.x < 0) {
                    params.x = 0;
                }
                if (params.y < 0) {
                    params.y = 0;
                }


                wm.updateViewLayout(myview, params);
//                lastAction = event.getAction();
                break;
            case MotionEvent.ACTION_UP:
//                touchcheck();
                view.setBackgroundResource(R.drawable.walk);
                walk_an = (AnimationDrawable)view.getBackground();
                walk_an.start();
                sticky=false;
                handler.post(draw());
                break;
            case MotionEvent.ACTION_OUTSIDE:
        }
        return false;
    }
}


    private void ShimejiView()
    {


        li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        myview = LayoutInflater.from(this).inflate(R.layout.playground, null);

        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;

        params.x = 0;
        params.y = (int) (height / 2);
        view = myview.findViewById(R.id.demo);
        view.setBackgroundResource(R.drawable.walk); // Set the animation drawable
        walk_an = (AnimationDrawable) view.getBackground(); // Get the animation
        walk_an.start(); // Start the animation

        // Add the view to the window
        wm.addView(myview, params);
        view.setOnTouchListener(new action());

        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }
    //View
    private void randomsens()
    {
        Random s = new Random();
        timing = new CountDownTimer(8000,(s.nextInt(5000)+5000))
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                Random r = new Random();
                sens = r.nextInt(6) + 1;
            }
            @Override
            public void onFinish() {
                timing.start();
            }
        }.start();
    }
    private int minLimitX;
    private int maxLimitX;
    private int minLimitY;
    private int maxLimitY;

    private Runnable draw() {
        if (sticky) return null;
        sticky = false;
        Log.e("drawLog", "draw đang hoạt động");
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        // Cập nhật giới hạn
        minLimitX = 0;
        maxLimitX = screenWidth-view.getHeight(); // Giới hạn bên phải
        minLimitY = 0;
        maxLimitY = screenHeight - view.getHeight();
        running = new CountDownTimer(10000, 16) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("param", "onTick: " + params.x +" + "+ params.y );
                switch (sens) {
                    case 1: // Di chuyển sang phải
                        if (params.x < maxLimitX) {
                            params.x++;
                            right();
                        } else {
                            sens = 2; // Đổi hướng sang trái
                        }
                        break;

                    case 2: // Di chuyển sang trái
                        if (params.x > minLimitX) {
                            params.x--;
                            left();
                        } else {
                            sens = 1; // Đổi hướng sang phải
                        }
                        break;

                    case 3: // Di chuyển chéo lên phải
                        if (params.x < maxLimitX && params.y > minLimitY) {
                            params.x++;
                            params.y--;
                            right();
                            // up();
                        } else {
                            sens = 4; // Đổi hướng chéo lên trái
                        }
                        break;

                    case 4: // Di chuyển chéo lên trái
                        if (params.x > minLimitX && params.y > minLimitY) {
                            params.x--;
                            params.y--;
                            left();
                            // up();
                        } else {
                            sens = 3; // Đổi hướng chéo xuống phải
                        }
                        break;

                    case 5: // Di chuyển chéo xuống phải
                        if (params.x < maxLimitX && params.y < maxLimitY) {
                            params.x++;
                            params.y++;
                            right();
                            // down();
                        } else {
                            sens = 6; // Đổi hướng chéo xuống trái
                        }
                        break;

                    case 6: // Di chuyển chéo xuống trái
                        if (params.x > minLimitX && params.y < maxLimitY) {
                            params.x--;
                            params.y++;
                            left();
                            // down();
                        } else {
                            sens = 5; // Đổi hướng chéo lên phải
                        }
                        break;
                }

                // Cập nhật lại view sau khi thay đổi params
                wm.updateViewLayout(myview, params);
            }
            @Override
            public void onFinish() {
                running.start(); // Khởi động lại khi kết thúc
            }
        }.start();
        return null;
    }



    private void left()
    {
        restoreView(view);
    }
    private void right()
    {
        flipView(view);
    }

    // Phương thức để lật
    private void flipView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "scaleX", -1f);
        animator.setDuration(50);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isFlipped = true;
            }
        });
        animator.start();
    }

    // Phương thức để khôi phục lại trạng thái bình thường
    private void restoreView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        animator.setDuration(50);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isFlipped = false; // Cập nhật trạng thái
            }
        });
        animator.start();
    }
    public void stopDraw() {
        Log.e("drawLog", "draw Stop hoạt động");
        if (running != null) {
            running.cancel(); // Dừng timer
            running = null; // Đặt lại để tránh lỗi
        }
        sticky = true;
    }

    @SuppressLint("SuspiciousIndentation")
    public void onReceiveNotify(String notify) {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        textView = myview.findViewById(R.id.content_notify_view_id); // Nếu bạn có một TextView khác bên trái



        if (textView != null) {
            Log.e("BubbleModule", notify);
            stopDraw();
            if (notify != null && !notify.equals("0")) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(notify);
//                handler.post(flipChecker);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sticky=false;
                        handler.post(draw());
                        textView.setVisibility(View.GONE); // Dừng kiểm tra trạng thái flip
                    }
                }, 5000);
            } else {
                textView.setVisibility(View.GONE);
                handler.post(draw());
            }
        } else {
            Log.e("BubbleModule", "TextView is null");
        }
    }




}
