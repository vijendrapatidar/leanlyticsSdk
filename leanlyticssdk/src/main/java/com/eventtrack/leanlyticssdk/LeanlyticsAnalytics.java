package com.eventtrack.leanlyticssdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class LeanlyticsAnalytics {

    private static final String TAG = "LeanlyticsAnalytics";
    private static LeanlyticsAnalytics instance;
    private boolean isTimerStart;
    private ArrayList<String> classes;
    private int width = 0, height = 0;
    public int totalTimeDuration = 0;
    public String startTime;
    public String endTime;
    public Application application;
    public String androidDeviceId;
    public String appId;
    private SimpleDateFormat dateFormat;
    private Activity mActivity;
    private String singleClassName;

    public static LeanlyticsAnalytics getInstance() {
        return instance;
    }

    public static void initInstance() {
        instance = new LeanlyticsAnalytics();
    }

    public void start(Application app, String appId) {
        classes = new ArrayList<>();

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

        AppVisibilityDetector.init(app, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                startTime();
                isTimerStart = true;
                Log.d("StartTime Foreg", "" + totalTimeDuration);
                Log.e(TAG, "onAppGotoForeground: ");
                startTakingScreenShot();
            }

            @Override
            public void onAppGotoBackground() {
                startTime();
                isTimerStart = false;
                endTime = dateFormat.format(new Date());
                Log.d("StartTime Backgr", "" + totalTimeDuration);
                Log.e(TAG, "onAppGotoBackground: ");
                startTakingScreenShot();
            }

            @Override
            public void onAppFinish() {
                Log.d("StartTime onAppFinish", "" + totalTimeDuration);
                //startTime(false);
            }

            @Override
            public void sendActivity(Activity activity) {
                mActivity = activity;
                Log.e("class name", activity.getLocalClassName());
                singleClassName = activity.getLocalClassName();

                Display display = activity.getWindowManager().getDefaultDisplay();
                width = display.getWidth();
                height = display.getHeight();

                Log.e("screen resoulation", width + "\n" + height);
            }
        });

        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isTimerStart) {
                    View view = mActivity.getWindow().getDecorView().getRootView();
                    takeScreenshot(view);
                    handler.postDelayed(this, 2000);
                }
            }
        }, 2000);*/
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

    private void startTakingScreenShot() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isTimerStart) {
                    View view = mActivity.getWindow().getDecorView().getRootView();
                    takeScreenshot(view);
                    startTakingScreenShot();
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

    private void store(Bitmap bm, String fileName) {
        if (!(bm.getWidth() == 0 && bm.getHeight() == 0)) {
            final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
            File dir = new File(dirPath);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dirPath, fileName + ".jpeg");
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
                uploadScreenShot(appId, singleClassName, "" + file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap takeScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        if (!classes.contains(singleClassName)) {
            store(bitmap, "" + System.currentTimeMillis());
        }

        Log.e("bitmap", "" + bitmap);

        return bitmap;
    }

    private void uploadScreenShot(String appId, String className, String file) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("applicationId", appId);
        hm.put("className", className);
        hm.put("width", "" + 300);
        hm.put("height", "" + 600);
        new ImageWebServicePost("http://159.89.164.34:4100/api/v1/users/create/screenshot", hm, file, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                try {
                    JSONObject object = new JSONObject(responseData);
                    if (object.getBoolean("success")) {
                        Log.e(TAG, "onTaskDone: Image" + responseData);
                        classes.add(singleClassName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Log.e(TAG, "onTaskDone: error");
            }
        }).execute();
    }
}