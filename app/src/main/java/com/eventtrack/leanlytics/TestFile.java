package com.eventtrack.leanlytics;

import android.app.Application;
import com.relibit.pulsemetrics.PulseAnalytics;

public class TestFile {

    public static void updateSync(Application application, String id) {
        PulseAnalytics.initInstance();
        PulseAnalytics.getInstance().start(application, id);
    }
}
