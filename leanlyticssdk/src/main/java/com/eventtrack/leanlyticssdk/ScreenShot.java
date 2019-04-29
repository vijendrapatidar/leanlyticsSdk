package com.eventtrack.leanlyticssdk;

import android.graphics.Bitmap;

import java.util.HashMap;

public class ScreenShot {
    private String className;
    private Bitmap  file;
    private HashMap<String, String> screenShotData;

    public ScreenShot(String className, Bitmap  file, HashMap<String, String> hashMap) {
        this.className = className;
        this.file = file;
        this.screenShotData = hashMap;
    }

    public String getClassName() {
        return className;
    }

    public Bitmap  getFile() {
        return file;
    }

    public HashMap<String, String> getScreenShotData() {
        return screenShotData;
    }
}
