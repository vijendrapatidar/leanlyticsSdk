package com.eventtrack.leanlyticssdk;

import android.os.AsyncTask;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WebServiceForPost extends AsyncTask<String, Void, String> {

    private OnTaskDoneListener onTaskDoneListener;
    private String urlStr = "";
    private HashMap<String, Object> hm;

    public WebServiceForPost(String url, HashMap<String, Object> hm, OnTaskDoneListener onTaskDoneListener) {
        this.urlStr = url;
        this.onTaskDoneListener = onTaskDoneListener;
        this.hm = hm;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";
        try {
            String urlParameters = getPostDataString(hm);
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            URL url = new URL("http://fltspc.itu.dk/widget/515318fe17450f312b00153d/");
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("PUT");
            httpConnection.setDoOutput(true);
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter osw = new OutputStreamWriter(httpConnection.getOutputStream());
            osw.write(getPostDataString(hm));
            osw.flush();
            osw.close();
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (onTaskDoneListener != null && s != null) {
            onTaskDoneListener.onTaskDone(s);
        } else
            onTaskDoneListener.onError();
    }

    private String getPostDataString(HashMap<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }

        return result.toString();
    }
}