package com.eventtrack.leanlyticssdk;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LeanlyticsAnalytics {
    private static final String TAG = "LeanlyticsAnalytics";
    private static LeanlyticsAnalytics instance;

    public static LeanlyticsAnalytics getInstance() {
        return instance;
    }

    public static void initInstance() {
        instance = new LeanlyticsAnalytics();
    }

    public void uploadScreenShot(boolean isUpload, View view) {
        Bitmap screenshot = null;
        try {
            if (view != null) {
                int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                view.measure(spec, spec);
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                screenshot = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(screenshot);
//                view.draw(canvas);
            }

        } catch (Exception e) {
            Log.d("ScreenShotActivity", "Failed to capture screenshot because:" + e.getMessage());
        }
    }


    public void start(Application app) {
        createAppSession(app);

        AppVisibilityDetector.init(app, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                Log.e(TAG, "onAppGotoForeground: ");
            }

            @Override
            public void onAppGotoBackground() {
                Log.e(TAG, "onAppGotoBackground: ");
            }

            @Override
            public void onAppFinish() {

            }
        });

    }


    private void createAppSession(Application app) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("applicationId", "OkRcYmRk1");
        hm.put("deviceId", "OkRcYmRk1dgdfg");
        hm.put("deviceType", "Android Pixel 2");
        hm.put("deviceOs", "Android");
        hm.put("deviceModel", "BND-LS");
        hm.put("location", "India");
        new WebServiceForPost(app, "http://159.89.164.34:4100/api/v1/users/create", hm, new OnTaskDoneListener() {
            @Override
            public void onTaskDone(String responseData) {
                Log.e(TAG, "onTaskDone: " + responseData);
            }

            @Override
            public void onError() {
                Log.e(TAG, "onTaskDone: error");
            }
        }).execute();
//        performPostCall("http://159.89.164.34:4100/api/v1/users/create", hm);
    }

    public String performPostCall(String requestURL,
                                  HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static void store(Bitmap bm, String fileName) {
        if (!(bm.getWidth() == 0 && bm.getHeight() == 0)) {
            final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
            File dir = new File(dirPath);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dirPath, fileName);
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getScreenShot(View view) {
//        View screenView = view.getRootView();
//        screenView.setDrawingCacheEnabled(true);
//        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
//        screenView.setDrawingCacheEnabled(false);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}