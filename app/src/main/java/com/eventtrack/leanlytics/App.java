package com.eventtrack.leanlytics;

import android.app.Application;
import cloud.com.testinglibrary.TestClass;

public class App extends Application {

    private String appId = "TSJUcgLGig2UOdjAzlqq4KW5GK6PKXHC2ndw40HYIss7EKsV6nJ";
    //private String appId = "SJUcgLGig2UOdjAzlqq4KW5GK6PKXHC2ndw40HYIss7EKsV6nJ";

    @Override
    public void onCreate() {
        super.onCreate();

        TestClass.getInstance().start(this, appId);
    }
}
