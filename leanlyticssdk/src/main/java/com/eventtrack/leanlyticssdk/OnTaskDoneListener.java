package com.eventtrack.leanlyticssdk;

public interface OnTaskDoneListener {
    void onTaskDone(String responseData);

    void onError();
}
