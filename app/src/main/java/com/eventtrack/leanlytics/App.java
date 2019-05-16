package com.eventtrack.leanlytics;

import android.app.Application;
import com.relibit.pulsemetrics.PulseAnalytics;

public class App extends Application {

    private String appId = "TSJUcgLGig2UOdjAzlqq4KW5GK6PKXHC2ndw40HYIss7EKsV6nJ";
    //private String appId = "SJUcgLGig2UOdjAzlqq4KW5GK6PKXHC2ndw40HYIss7EKsV6nJ";

    @Override
    public void onCreate() {
        super.onCreate();

        PulseAnalytics.initInstance();
        PulseAnalytics.getInstance().start(this, appId);
    }
}
