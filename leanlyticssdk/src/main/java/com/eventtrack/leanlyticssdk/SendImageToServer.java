package com.eventtrack.leanlyticssdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public class SendImageToServer extends AsyncTask<String, Void, String> {

    private OnTaskDoneListener onTaskDoneListener;
    private String urlStr = "";
    private HashMap<String, String> hm;
    private Bitmap fileName;

    SendImageToServer(String url, HashMap<String, String> hm, Bitmap file, OnTaskDoneListener onTaskDoneListener) {
        this.urlStr = url;
        this.onTaskDoneListener = onTaskDoneListener;
        this.hm = hm;
        this.fileName = file;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        try {

            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "file" + "\"; filename=\"" + hm.get("filename") + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + "image/*" + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            BitmapFactory.Options options = null;
            options = new BitmapFactory.Options();
            options.inSampleSize = 3;

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            fileName.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byte_arr = stream.toByteArray();

            outputStream.write(byte_arr);

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            Iterator<String> keys = hm.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = hm.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            if (200 != connection.getResponseCode()) {
                throw new Exception("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);

            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;

        } catch (Exception e) {
            try {
                throw new Exception("Failed to upload code:" + e.getMessage() + " " + e.getLocalizedMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (onTaskDoneListener != null && s != null) {
            onTaskDoneListener.onTaskDone(s);
        } else
            onTaskDoneListener.onError();
    }
}