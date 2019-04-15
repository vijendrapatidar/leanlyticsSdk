package com.eventtrack.leanlyticssdk;

import java.util.HashMap;

public class ScreenShot {
    private String className;
    private String file;
    private HashMap<String, String> screenShotData;

    public ScreenShot(String className, String file, HashMap<String, String> hashMap) {
        this.className = className;
        this.file = file;
        this.screenShotData = hashMap;
    }

    public String getClassName() {
        return className;
    }

    public String getFile() {
        return file;
    }

    public HashMap<String, String> getScreenShotData() {
        return screenShotData;
    }
}
