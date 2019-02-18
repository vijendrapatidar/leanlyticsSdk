package com.eventtrack.leanlyticssdk;

import android.content.Context;
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

    private Context mContext;
    private OnTaskDoneListener onTaskDoneListener;
    private String urlStr = "";
    private HashMap<String, String> hm;

    public WebServiceForPost(Context context, String url, HashMap<String, String> hm, OnTaskDoneListener onTaskDoneListener) {
        this.mContext = context;
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
            URL mUrl = new URL(urlStr);
            HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConnection.setRequestProperty( "charset", "utf-8");
            httpConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            httpConnection.setUseCaches( false );
            httpConnection.setConnectTimeout(100000);
            httpConnection.setReadTimeout(100000);
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            httpConnection.connect();


            OutputStream os = httpConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(hm));

            writer.flush();
            writer.close();
            os.close();
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
}