package com.eventtrack.leanlyticssdk;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class PostTimeService extends Service {


    public PostTimeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //Code here
        LeanlyticsAnalytics sdkInstance = LeanlyticsAnalytics.getInstance();
        postTotalDuration(sdkInstance.application,
                sdkInstance.androidDeviceId,
                sdkInstance.appId, sdkInstance.startTime,
                sdkInstance.endTime, sdkInstance.totalTimeDuration);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopSelf();
    }

    private static final String TAG = "PostTimeService";

    private void postTotalDuration(Application app, String androidId, String appId, String startTime, String endTime, int totalTimeDuration) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("applicationId", appId);
        hm.put("deviceId", androidId);
        hm.put("sessionId", UUID.randomUUID().toString());
        hm.put("start_time", startTime);
        hm.put("end_time", endTime);
        hm.put("duration", String.valueOf(totalTimeDuration));
        new WebServiceForPost(app, "http://159.89.164.34:4100/api/v1/users/session/create", hm, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                Log.e(TAG, "onTaskDone: Sagar" + responseData);
            }

            @Override
            public void onError() {
                Log.e(TAG, "onTaskDone: error");
            }
        }).execute();
    }
}
