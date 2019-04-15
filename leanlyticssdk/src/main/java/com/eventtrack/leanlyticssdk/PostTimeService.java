package com.eventtrack.leanlyticssdk;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class PostTimeService extends Service {

    private static final String TAG = "PostTimeService";

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

        saveSession(sdkInstance.application,
                sdkInstance.appId,
                sdkInstance.environment,
                sdkInstance.startTime,
                sdkInstance.endTime,
                sdkInstance.totalTimeDuration,
                sdkInstance.sessionFlowArray,
                sdkInstance.addressMap,
                sdkInstance.locationMap,
                sdkInstance.deviceId,
                sdkInstance.deviceType,
                sdkInstance.osType,
                sdkInstance.model,
                sdkInstance.width,
                sdkInstance.height);

        /*postTotalDuration(sdkInstance.application,
                sdkInstance.androidDeviceId,
                sdkInstance.appId, sdkInstance.startTime,
                sdkInstance.endTime, sdkInstance.totalTimeDuration);*/
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopSelf();
    }

    private void saveSession(Application app, String appId, String environment, String startTime, String endTime,
                             int duration, JSONArray session, JSONObject addressMap, JSONObject locationMap,
                             String deviceId, String deviceType, String os, String model, int width, int height) {
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("key", appId);
        hm.put("environment", environment);
        hm.put("startDateTime", startTime);
        hm.put("endDateTime", endTime);
        hm.put("duration", duration);
        hm.put("sessionFlow", session);
        hm.put("address", addressMap);
        hm.put("location", locationMap);
        hm.put("deviceId", deviceId);
        hm.put("deviceType", deviceType);
        hm.put("os", os);
        hm.put("deviceModel", model);
        hm.put("height", width);
        hm.put("width", height);
        new WebServiceForPost(LeanlyticsAnalytics.baseUrl + "session", hm, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                Log.e("sagar", responseData);
            }

            @Override
            public void onError() {
                Log.e("sagar", "onTaskDone: error");
            }
        }).execute();
    }

     /*private void postTotalDuration(Application app, String androidId, String appId, String startTime, String endTime, int totalTimeDuration) {
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("applicationId", appId);
        hm.put("deviceId", androidId);
        hm.put("sessionId", UUID.randomUUID().toString());
        hm.put("start_time", startTime);
        hm.put("end_time", endTime);
        hm.put("duration", String.valueOf(totalTimeDuration));
        new WebServiceForPost("http://159.89.164.34:4100/api/v1/users/session/create", hm, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                Log.e(TAG, "onTaskDone:" + responseData);
            }

            @Override
            public void onError() {
                Log.e(TAG, "onTaskDone: error");
            }
        }).execute();
    }*/
}
