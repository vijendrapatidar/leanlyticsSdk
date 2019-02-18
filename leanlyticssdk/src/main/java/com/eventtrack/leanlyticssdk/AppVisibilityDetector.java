/*
 * Class to detect the app is in foreground or background
 *
 * */

package com.eventtrack.leanlyticssdk;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.List;

public final class AppVisibilityDetector {
    private static final int MSG_GOTO_FOREGROUND = 1;
    private static final int MSG_GOTO_BACKGROUND = 2;
    private static final int MSG_FINISHAPP = 3;
    private static AppVisibilityCallback sAppVisibilityCallback;
    private static boolean sIsForeground = false;
    private static Handler sHandler;

    public static void init(final Application app, AppVisibilityCallback appVisibilityCallback) {
        if (!checkIsMainProcess(app)) {
            return;
        }
        sAppVisibilityCallback = appVisibilityCallback;
        app.registerActivityLifecycleCallbacks(new AppActivityLifecycleCallbacks());

        sHandler = new Handler(app.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_GOTO_FOREGROUND:
                        performAppGotoForeground();
                        break;
                    case MSG_GOTO_BACKGROUND:
                        performAppGotoBackground();
                        break;
                    case MSG_FINISHAPP:
                        performAppFinish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private static boolean checkIsMainProcess(Application app) {
        ActivityManager activityManager = (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        if (null == runningAppProcessInfoList) {
            return false;
        }

        String currProcessName = null;
        int currPid = android.os.Process.myPid();
        //find the process name
        for (RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (null != processInfo && processInfo.pid == currPid) {
                currProcessName = processInfo.processName;
            }
        }

        //is current process the main process
        if (!TextUtils.equals(currProcessName, app.getPackageName())) {
            return false;
        }

        return true;
    }

    private static void performAppGotoForeground() {
        if (!sIsForeground && null != sAppVisibilityCallback) {
            sIsForeground = true;
            sAppVisibilityCallback.onAppGotoForeground();
        }
    }

    private static void performAppGotoBackground() {
        if (sIsForeground && null != sAppVisibilityCallback) {
            sIsForeground = false;
            sAppVisibilityCallback.onAppGotoBackground();
        }
    }

    private static void performAppFinish() {
        if (null != sAppVisibilityCallback) {
            sIsForeground = false;
            sAppVisibilityCallback.onAppFinish();
        }
    }

    public interface AppVisibilityCallback {
        void onAppGotoForeground();

        void onAppGotoBackground();

        void onAppFinish();
    }

    private static class AppActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        int activityDisplayCount = 0;


        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            sHandler.removeMessages(MSG_GOTO_FOREGROUND);
            sHandler.removeMessages(MSG_GOTO_BACKGROUND);
            if (activityDisplayCount == 0) {
                sHandler.sendEmptyMessage(MSG_GOTO_FOREGROUND);
            }
            activityDisplayCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            sHandler.removeMessages(MSG_GOTO_FOREGROUND);
            sHandler.removeMessages(MSG_GOTO_BACKGROUND);
            if (activityDisplayCount > 0) {
                activityDisplayCount--;
            }

            if (activityDisplayCount == 0) {
                sHandler.sendEmptyMessage(MSG_GOTO_BACKGROUND);
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {

            sHandler.sendEmptyMessage(MSG_FINISHAPP);
        }
    }
}