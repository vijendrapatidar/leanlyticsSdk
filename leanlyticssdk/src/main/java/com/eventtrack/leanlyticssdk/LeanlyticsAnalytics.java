package com.eventtrack.leanlyticssdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LeanlyticsAnalytics {
    private static final String TAG = "LeanlyticsAnalytics";
    private static LeanlyticsAnalytics instance;
    private boolean isTimerStart;

    public static LeanlyticsAnalytics getInstance() {
        return instance;
    }

    public static void initInstance() {
        instance = new LeanlyticsAnalytics();
    }

    public int totalTimeDuration = 0;
    public String startTime;
    public String endTime;
    public Application application;
    public String androidDeviceId;
    public String appId;
    private SimpleDateFormat dateFormat;

    public void start(Application app, String appId) {

        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(app.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        createAppSession(app, android_id, appId);
        this.androidDeviceId = android_id;
        this.appId = appId;
        this.application = app;
        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        this.startTime = dateFormat.format(new Date());

        Log.d("DeviceId", "" + android_id);
        app.startService(new Intent(app, PostTimeService.class));
        //PostTimeService postTimeService=new PostTimeService(startTimeValue);
        AppVisibilityDetector.init(app, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                startTime();
                isTimerStart = true;
                Log.d("StartTime Foreg", "" + totalTimeDuration);
                Log.e(TAG, "onAppGotoForeground: ");
            }

            @Override
            public void onAppGotoBackground() {
                startTime();
                isTimerStart = false;
                endTime = dateFormat.format(new Date());
                Log.d("StartTime Backgr", "" + totalTimeDuration);
                Log.e(TAG, "onAppGotoBackground: ");
            }

            @Override
            public void onAppFinish() {
                Log.d("StartTime onAppFinish", "" + totalTimeDuration);
//                startTime(false);
            }
        });

        ActivityManager am = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        try {
            final Context otherAppContext = application.getApplicationContext().createPackageContext(cn.getPackageName(),
                    Context.CONTEXT_IGNORE_SECURITY |
                            Context.CONTEXT_INCLUDE_CODE);

            //View rootView = ((Activity)otherAppContext).findViewById(android.R.id.content).getRootView();

            Log.e("root view", ""+otherAppContext);
           /* new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    View view = ((Activity) otherAppContext.getApplicationContext()).getWindow().getDecorView().getRootView();
                    takeScreenshot(view);
                }
            }, 2000);*/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return application.getApplicationContext().getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


    private void startTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isTimerStart) {
                    totalTimeDuration++;
                    startTime();
                }
            }
        }, 1000);
    }


    private void createAppSession(Application app, String androidId, String appId) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("applicationId", appId);
        hm.put("deviceId", androidId);
        hm.put("deviceType", "Android");
        hm.put("deviceOs", "Android 8.1");
        hm.put("deviceModel", "BND-LS");
        hm.put("location", "India");
        new WebServiceForPost(app, "http://159.89.164.34:4100/api/v1/users/create", hm, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                Log.e(TAG, "onTaskDone: " + responseData);
            }

            @Override
            public void onError() {
                Log.e(TAG, "onTaskDone: error");
            }
        }).execute();
    }

    public void store(Bitmap bm, String fileName) {
        if (!(bm.getWidth() == 0 && bm.getHeight() == 0)) {
            final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
            File dir = new File(dirPath);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dirPath, fileName);
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    public Bitmap getScreenShot(View view) {
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache(true);
//        Log.e("screen shot bitmap", "" + view.getDrawingCache());
//        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, 720, 1280);
//        view.setDrawingCacheEnabled(false);
//        return bitmap;
//    }

    public Bitmap takeScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        Log.e("bitmap", "" + bitmap);

        return bitmap;
    }
}