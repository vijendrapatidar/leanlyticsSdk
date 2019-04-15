package com.eventtrack.leanlytics;

import android.app.Application;
import com.eventtrack.leanlyticssdk.LeanlyticsAnalytics;

public class App extends Application {


    private String appId = "TSJUcgLGig2UOdjAzlqq4KW5GK6PKXHC2ndw40HYIss7EKsV6nJ";
    //private String appId = "SJUcgLGig2UOdjAzlqq4KW5GK6PKXHC2ndw40HYIss7EKsV6nJ";

    @Override
    public void onCreate() {
        super.onCreate();
        LeanlyticsAnalytics.initInstance();
        LeanlyticsAnalytics.getInstance().start(this, appId);
    }
}
