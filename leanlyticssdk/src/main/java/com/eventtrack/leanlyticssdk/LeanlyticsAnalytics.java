package com.eventtrack.leanlyticssdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class LeanlyticsAnalytics {

    static String baseUrl = "http://192.168.1.137:3001/";
    //public static String baseUrl = "http://13.56.140.11:3001/";

    private final String TAG = "Leanlytics";
    private static LeanlyticsAnalytics instance;

    private SimpleDateFormat dateFormat;

    private Activity mActivity;

    Application application;

    private boolean isTimerStart = false;
    private boolean isScreenShotUpload = false;

    int totalTimeDuration = 0;
    int width = 0, height = 0;
    private int screenDuration = 0;
    private int statusBarHeight = 0;
    private int previousX = 0, previousY = 0;

    String startTime;
    String endTime;
    String appId;
    String environment = "live";
    String deviceType = "android";
    String deviceId = "";
    String osType = "";
    String model = "";
    private String singleClassName = "";

    private ArrayList<String> classes;
    private ArrayList<ScreenShot> listScreeShot;
    private ArrayList<EventDetail> listEvents;
    private ArrayList<EventDetail> list;

    JSONObject addressMap, locationMap;
    JSONObject sessionObject, eventObject;

    JSONArray sessionFlowArray, eventArray;

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(baseUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Socket getSocket() {
        return mSocket;
    }

    public static LeanlyticsAnalytics getInstance() {
        return instance;
    }

    public static void initInstance() {
        instance = new LeanlyticsAnalytics();
    }

    public void start(final Application app, final String appId) {
        classes = new ArrayList<>();
        addressMap = new JSONObject();
        locationMap = new JSONObject();

        sessionFlowArray = new JSONArray();
        eventArray = new JSONArray();

        sessionObject = new JSONObject();
        eventObject = new JSONObject();

        listScreeShot = new ArrayList<>();
        listEvents = new ArrayList<>();
        list = new ArrayList<>();

        setUpSocket();

        this.appId = appId;
        this.application = app;

        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(application.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        this.deviceId = android_id;

        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        this.startTime = dateFormat.format(new Date());

        model = getDeviceName();
        osType = versionName();

        Log.e(TAG + " deviceId", "" + android_id);
        application.startService(new Intent(application, PostTimeService.class));

        getLocationUser();

        appVisibilityIdentifier();

        getEventsScreenShot(appId);

    }

    private void appVisibilityIdentifier() {
        AppVisibilityDetector.init(application, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                Log.e(TAG + " ffff 3", "");
                updateOnlineStatus();
                startTime();
                isTimerStart = true;
                Log.e(TAG, "" + totalTimeDuration);
                Log.e(TAG, "onAppGotoForeground: ");
                Log.e(TAG + " Np", "1");
            }

            @Override
            public void onAppGotoBackground() {
                Log.e(TAG + " ffff 4", "");
                updateOfflineStatus();
                startTime();
                isTimerStart = false;
                endTime = dateFormat.format(new Date());
                Log.e(TAG + " time back", "" + totalTimeDuration);
                Log.e(TAG + " app status", "onAppGotoBackground: ");
                Log.e(TAG + " Np", "2");
            }

            @Override
            public void onAppFinish() {
                //updateOfflineStatus();
                Log.e(TAG + " time for", "" + totalTimeDuration);
            }

            @Override
            public void sendActivity(Activity activity) {
                if (classes.size() != 0) {
                    Log.e(TAG + " ffff 5", singleClassName);
                    updateOfflineStatus();
                }
                Log.e(TAG + " Np", "3");
                if (!singleClassName.equalsIgnoreCase("")) {
                    try {
                        sessionObject = new JSONObject();
                        sessionObject.put("className", singleClassName);
                        sessionObject.put("screenDuration", screenDuration);
                        sessionObject.put("events", eventArray);

                        sessionFlowArray.put(sessionObject);

                        Log.e(TAG + " session arr", "" + sessionFlowArray.length());

                        screenDuration = 0;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mActivity = activity;
                singleClassName = activity.getClass().getSimpleName();

                Display display = activity.getWindowManager().getDefaultDisplay();
                width = display.getWidth();
                height = display.getHeight();

                Log.e(TAG + " class name", activity.getClass().getSimpleName());
                Log.e(TAG + " screen res", width + "\n" + height);

                if (!classes.contains(singleClassName)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            View view = mActivity.getWindow().getDecorView().getRootView();
                            takeScreenshot(view);
                            isScreenShotUpload = true;
                        }
                    }, 500);
                }

                mActivity.getWindow().setCallback(new MyWindowCallback(mActivity.getWindow().getCallback()));

                filterListByClass(singleClassName);

                getStatusBarHeight();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG + " ffff 6", singleClassName);
                        updateOnlineStatus();
                    }
                }, 500);
            }
        });
    }

    private void filterListByClass(String singleClassName) {
        if (listEvents.size() > 0) {
            for (int i = 0; i < listEvents.size(); i++) {
                if (listEvents.get(i).getControlName().equalsIgnoreCase(singleClassName)) {
                    list.add(listEvents.get(i));
                    return;
                }
            }
        }
    }

    private void startTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isTimerStart) {
                    totalTimeDuration++;
                    screenDuration++;
                    startTime();
                }
            }
        }, 1000);
    }

    private void getLocationUser() {
        Log.e(TAG + " position", "2");
        new WebServiceForGet("http://ip-api.com/json", new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                Log.e(TAG + " loc response", "" + responseData);

                try {
                    JSONObject object = new JSONObject(responseData);

                    addressMap.put("city", object.getString("city"));
                    addressMap.put("country", object.getString("country"));
                    locationMap.put("lat", Double.toString(object.getDouble("lat")));
                    locationMap.put("lng", Double.toString(object.getDouble("lon")));

                    createUser(appId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Log.e(TAG + "url response", "error");
            }
        }).execute();
    }

    private void createUser(String appId) {
        Log.e(TAG + " position", "3");
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("key", appId);
        hm.put("environment", environment);
        hm.put("address", addressMap);
        hm.put("location", locationMap);
        hm.put("deviceId", deviceId);
        hm.put("deviceType", deviceType);
        hm.put("os", osType);
        hm.put("deviceModel", model);
        hm.put("height", width);
        hm.put("width", height);
        new WebServiceForPost(baseUrl + "user-session", hm, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                //Log.e(TAG + " sagar", responseData);
                Log.e(TAG + " position", "4");
            }

            @Override
            public void onError() {
                Log.e(TAG + " ", "onTaskDone: error");
            }
        }).execute();
    }


    private void getEventsScreenShot(final String appId) {
        new WebServiceForGet(baseUrl + "screenshots/" + appId, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                listEvents.clear();
                Log.e(TAG + " response", "" + responseData);
                try {
                    JSONObject object = new JSONObject(responseData);
                    if (object.getBoolean("success")) {
                        Log.e(TAG + " list screen", "" + object.getJSONArray("data"));
                        JSONArray array = object.getJSONArray("data");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject data = array.getJSONObject(i);

                            JSONArray eventArray = data.getJSONArray("eventTags");
                            String control = data.getString("controlName");
                            String id = data.getString("_id");

                            ArrayList<Event> listEvent = new ArrayList<>();

                            if (eventArray.length() > 0) {
                                for (int j = 0; j < eventArray.length(); j++) {
                                    String label = null, labelId = null;
                                    Double[] eventFloat = new Double[4];
                                    JSONObject innerObject = eventArray.getJSONObject(j);
                                    if (innerObject != null) {
                                        label = innerObject.getString("label");
                                        labelId = innerObject.getString("_id");
                                        JSONArray eventStringArray = innerObject.getJSONArray("coordinates");
                                        ArrayList<String> coordinates = new ArrayList<String>();

                                        for (int k = 0; k < eventStringArray.length(); k++) {
                                            coordinates.add(eventStringArray.get(k).toString());
                                        }

                                        double x1 = Float.parseFloat(coordinates.get(0)) * width;
                                        double y1 = Float.parseFloat(coordinates.get(1)) * height;
                                        double x2 = Float.parseFloat(coordinates.get(2)) * width;
                                        double y2 = Float.parseFloat(coordinates.get(3)) * height;

                                        double widthR = x2 - x1;
                                        double heightR = y2 - y1;

                                        eventFloat[0] = x1;
                                        eventFloat[1] = y1;
                                        eventFloat[2] = widthR;
                                        eventFloat[3] = heightR;
                                    }

                                    listEvent.add(new Event(label, labelId, eventFloat));
                                }

                            }

                            listEvents.add(new EventDetail(control, id, listEvent));
                            Log.e(TAG + "onTaskDone: ", "" + listEvents.get(0));
                        }

                        filterListByClass(singleClassName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Log.e(TAG + " url res", "error");
            }
        }).execute();
    }

    private void takeScreenshot(View view) {
        try {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(false);
            store(bitmap, "" + System.currentTimeMillis());

            Log.e(TAG + " bitmap", "" + bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void store(Bitmap bm, String fileName) {
        if (!(bm.getWidth() == 0 && bm.getHeight() == 0)) {
            final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
            File dir = new File(dirPath);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dirPath, fileName + ".png");
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();

                HashMap<String, String> hm = new HashMap<>();
                hm.put("key", appId);
                hm.put("environment", environment);
                hm.put("className", singleClassName);
                hm.put("width", "" + width);
                hm.put("height", "" + height);
                hm.put("deviceType", deviceType);
                hm.put("deviceId", deviceId);
                hm.put("deviceModel", model);
                hm.put("os", osType);
                hm.put("filename", singleClassName + ".png");

                classes.add(singleClassName);
                listScreeShot.add(new ScreenShot(singleClassName, "" + file, hm));
                Log.e(TAG + " upload: ", "" + listScreeShot.size() + "\n" + classes.size());

                if (!isScreenShotUpload) {
                    getListScreenShot(appId);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadScreenShotWithData(HashMap<String, String> hashMap, String file) {
        new ImageWebServicePost(baseUrl + "create-screenshot", hashMap, file, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                Log.e(TAG, "Main call");
                try {
                    JSONObject object = new JSONObject(responseData);
                    if (object.getBoolean("success")) {
                        isScreenShotUpload = false;
                        Log.e(TAG, "before upload " + listScreeShot.size());
                        if (listScreeShot.size() > 0) {
                            listScreeShot.remove(listScreeShot.size() - 1);
                            Log.e(TAG, "after upload " + listScreeShot.size());
                            getListScreenShot(appId);
                            getEventsScreenShot(appId);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Log.e(TAG, "onTaskDone: error");
            }
        }).execute();
    }

    private void getListScreenShot(final String appId) {
        new WebServiceForGet(baseUrl + "mobile-screenshots/" + appId, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                Log.e(TAG + " response", "" + responseData);
                try {
                    JSONObject object = new JSONObject(responseData);
                    if (object.getBoolean("success")) {
                        Log.e(TAG + " list screen", "" + object.getJSONArray("data"));
                        JSONArray array = object.getJSONArray("data");

                        if (array.length() > 0) {
                            if (listScreeShot.size() > 0) {
                                boolean isScreenFound = false;
                                ScreenShot screenShot = listScreeShot.get(listScreeShot.size() - 1);

                                for (int i = 0; i < array.length(); i++) {
                                    if (array.getString(i).equalsIgnoreCase(screenShot.getClassName())) {
                                        isScreenFound = true;
                                        isScreenShotUpload = false;
                                        listScreeShot.remove(listScreeShot.size() - 1);
                                        break;
                                    }
                                }

                                if (!isScreenFound) {
                                    uploadScreenShotWithData(screenShot.getScreenShotData(), screenShot.getFile());
                                }
                            }

                        } else {
                            if (listScreeShot.size() > 0) {
                                ScreenShot screenShot = listScreeShot.get(listScreeShot.size() - 1);
                                listScreeShot.remove(listScreeShot.size() - 1);
                                uploadScreenShotWithData(screenShot.getScreenShotData(), screenShot.getFile());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Log.e(TAG + " url res", "error");
            }
        }).execute();
    }

    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private String versionName() {
        return "Android " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
    }

    private int getStatusBarHeight() {
        int resourceId = mActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = mActivity.getResources().getDimensionPixelSize(resourceId);
        }

        Log.e(TAG + " sagar", "" + statusBarHeight);
        return statusBarHeight;
    }

    private void findEventName(int x, int y) {
        if (list.size() > 0) {
            if (list.get(0).getEvent().size() > 0) {
                for (int i = 0; i < list.get(0).getEvent().size(); i++) {
                    Event event = list.get(0).getEvent().get(i);
                    if (x >= event.getCoordinates()[0]
                            && y >= event.getCoordinates()[1]
                            && x <= event.getCoordinates()[0] + event.getCoordinates()[2]
                            && y <= (event.getCoordinates()[1] + event.getCoordinates()[3] + statusBarHeight)) {
                        //Log.e(TAG + "Sagar", event.getLabel());
                        Toast.makeText(mActivity, event.getLabel(), Toast.LENGTH_SHORT).show();
                        eventArray.put(new SendEvents(event.getLabel(), event.getCoordinates()));
                        return;
                    }
                }
            }
        }
    }


    /*---------------------------Socket performance-------------------------------------*/
    private void setUpSocket() {
        mSocket = getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.connect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG + " sagar", "connected");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG + " sagar", "disconnected");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG + " sagar", "error");
        }
    };

    private Emitter updateOnlineStatus() {
        Log.e(TAG + " ffff 2", singleClassName);
        JSONObject hm = new JSONObject();
        try {
            hm.put("key", appId);
            hm.put("location", locationMap);
            hm.put("address", addressMap);
            hm.put("className", singleClassName);
            hm.put("deviceId", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSocket.connected()) return
                mSocket.emit("userOnline", hm);
        return null;
    }

    public Emitter updateOfflineStatus() {
        Log.e(TAG + " ffff 1", singleClassName);
        JSONObject hm = new JSONObject();
        try {
            hm.put("key", appId);
            hm.put("location", locationMap);
            hm.put("address", addressMap);
            hm.put("className", singleClassName);
            hm.put("deviceId", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSocket.connected()) return
                mSocket.emit("userOffline", hm);
        return null;
    }


    class MyWindowCallback implements Window.Callback {

        Window.Callback localCallback;

        MyWindowCallback(Window.Callback localCallback) {
            this.localCallback = localCallback;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return localCallback.dispatchKeyEvent(event);
        }

        @SuppressLint("NewApi")
        @Override
        public boolean dispatchKeyShortcutEvent(KeyEvent event) {
            return localCallback.dispatchKeyShortcutEvent(event);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            //Log.e(TAG + "button ====>", "" + x + "\n" + y);
            if (x != previousX && y != previousY) {
                findEventName(x, y);
                previousX = x;
                previousY = y;
            }
            return localCallback.dispatchTouchEvent(event);
        }

        @Override
        public boolean dispatchTrackballEvent(MotionEvent event) {
            return localCallback.dispatchTrackballEvent(event);
        }

        @SuppressLint("NewApi")
        @Override
        public boolean dispatchGenericMotionEvent(MotionEvent event) {
            return localCallback.dispatchGenericMotionEvent(event);
        }

        @Override
        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
            return localCallback.dispatchPopulateAccessibilityEvent(event);
        }

        @Override
        public View onCreatePanelView(int featureId) {
            return localCallback.onCreatePanelView(featureId);
        }

        @Override
        public boolean onCreatePanelMenu(int featureId, Menu menu) {
            return localCallback.onCreatePanelMenu(featureId, menu);
        }

        @Override
        public boolean onPreparePanel(int featureId, View view, Menu menu) {
            return localCallback.onPreparePanel(featureId, view, menu);
        }

        @Override
        public boolean onMenuOpened(int featureId, Menu menu) {
            return localCallback.onMenuOpened(featureId, menu);
        }

        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item) {
            return localCallback.onMenuItemSelected(featureId, item);
        }

        @Override
        public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
            localCallback.onWindowAttributesChanged(attrs);
        }

        @Override
        public void onContentChanged() {
            localCallback.onContentChanged();
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            Log.d(TAG + "", "ttest onfocus changed called");
            localCallback.onWindowFocusChanged(hasFocus);
        }

        @Override
        public void onAttachedToWindow() {
            localCallback.onAttachedToWindow();
        }

        @Override
        public void onDetachedFromWindow() {
            localCallback.onDetachedFromWindow();
        }

        @Override
        public void onPanelClosed(int featureId, Menu menu) {
            localCallback.onPanelClosed(featureId, menu);
        }

        @Override
        public boolean onSearchRequested() {
            return localCallback.onSearchRequested();
        }

        @Override
        public boolean onSearchRequested(SearchEvent searchEvent) {
            return false;
        }

        @SuppressLint("NewApi")
        @Override
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
            return localCallback.onWindowStartingActionMode(callback);
        }

        @Override
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
            return null;
        }

        @SuppressLint("NewApi")
        @Override
        public void onActionModeStarted(ActionMode mode) {
            localCallback.onActionModeStarted(mode);

        }

        @SuppressLint("NewApi")
        @Override
        public void onActionModeFinished(ActionMode mode) {
            localCallback.onActionModeFinished(mode);

        }
    }
}