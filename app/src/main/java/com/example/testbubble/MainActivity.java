package com.example.testbubble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private Button button;


    private boolean hasOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return Settings.canDrawOverlays(this);
    }

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 100;
    private boolean isOverlayPermissionRequested = false;

    private boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (!Settings.canDrawOverlays(this)) {
            if (!isOverlayPermissionRequested) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Activity currentActivity = MainActivity.this;
                if (currentActivity != null) {
                    currentActivity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
                    isOverlayPermissionRequested = true;
                }
            }

            return false;
        }

        return true;
    }


    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         // Thiết lập FLAG_SHOW_WALLPAPER
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        setContentView(R.layout.activity_main);

        Switch startSwitch = findViewById(R.id.startBtn);

        startSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Bắt đầu dịch vụ khi switch được bật
                    if (!checkDrawOverlayPermission()) {
                        return;
                    }
                    if (!isServiceRunning(TestAction.class)) {
                        startService(new Intent(MainActivity.this, TestAction.class));
                    }
                } else {
                    if (isServiceRunning(TestAction.class)) {
                        Intent intent = new Intent(MainActivity.this, TestAction.class);
                        // Gọi phương thức stopServiceActions() trong TestAction
                        // Cách thực hiện này yêu cầu bạn cần lấy instance của TestAction
                        // Nhưng do Android không cho phép chúng ta làm điều này dễ dàng,
                        // ta có thể sử dụng LocalBroadcastManager hoặc Messenger để giao tiếp với dịch vụ.

                        // Ví dụ gửi broadcast:
                        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("STOP_SERVICE_ACTIONS"));
                    }
                }
            }
        });

    }
}