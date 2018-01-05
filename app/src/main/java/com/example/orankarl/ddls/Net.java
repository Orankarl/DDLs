package com.example.orankarl.ddls;

import android.text.LoginFilter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by orankarl on 2018/1/3.
 */

public class Net {
    private static String url = "http://45.77.183.226:5000";
    public static boolean isLogin = false;
    public static String token = "";
    public static String userId;
    public static OkHttpClient client;
    public static String username;

    public static String login(String username, String password) {
        try {
            if (client == null) client = new OkHttpClient();

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("username", username);
            params.put("password", password);
            Request request = new Request.Builder()
                    .url(attachHttpGetParams(url+"/api/login", params))
                    .build();
            Response response = client.newCall(request).execute();

//            Log.d("return value", response.body().string());

            JSONObject jsonObject = new JSONObject(response.body().string());
//            JSONArray jsonArray = new JSONArray(response.body().string());
//            //parseObject
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String status = jsonObject.getString("status");
                String reason = jsonObject.getString("reason");

                if (status.equals("1")) {
                    String token = jsonObject.getString("token");
                    isLogin = true;
                    Net.token = token;
                    Net.username = username;
                    return "";
                } else {
                    return reason;
                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No response";
    }

    public static String register(String username, String password) {
        try {
            if (client == null) client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .add("nickname", "2333")
                    .build();
            Request request = new Request.Builder()
                    .url(url+"/api/user")
                    .put(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            //parseObject
//            JSONArray jsonArray = new JSONArray(response.body().string());
//            Log.d("jsonlen", String.valueOf(jsonArray.length()));
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String status = jsonObject.getString("status");
                String reason = jsonObject.getString("reason");
                if (status.equals("1")) {
                    return "";
                } else {
                    return reason;
                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No response";
    }

    public static String queryDeadlineList() {
        try {
            if (client == null) client = new OkHttpClient();

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("token", token);
            Request request = new Request.Builder()
                    .url(attachHttpGetParams(url+"/api/deadline", params))
                    .build();
            Response response = client.newCall(request).execute();

//            Log.d("return value", response.body().string());

            JSONObject jsonObject = new JSONObject(response.body().string());
            String status = jsonObject.getString("status");
            String reason = jsonObject.getString("reason");
            if (status.equals("-1")) return reason;
            else {
                Log.d("parsing deadlines", "yes");
                JSONArray array = jsonObject.getJSONArray("deadlines");
                int len = array.length();
                List<Deadline> deadlineList = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    JSONObject json = array.getJSONObject(i);
                    deadlineList.add(new Deadline(json.getLong("id"),
                            json.getLong("time"), json.getString("title"),
                            json.getString("description"), Net.username, json.getBoolean("done")));
                }
                int code = DatabaseManager.insertDeadlines(deadlineList);
                if (code == 0) return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String addDeadline(Deadline deadline) {
        try {
            if (client == null) client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("token", token)
                    .add("title", deadline.getTitle())
                    .add("description", deadline.getInfo())
                    .add("time", String.valueOf(deadline.getCalendarMillis()))
                    .build();
            Request request = new Request.Builder()
                    .url(url+"/api/deadline")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            //parseObject
            String status = jsonObject.getString("status");
            String reason = jsonObject.getString("reason");
            if (status.equals("1")) {
                long id = jsonObject.getLong("id");
                return "";
            } else {
                return reason;
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No response";
    }

    public static String attachHttpGetParams(String url, LinkedHashMap<String,String> params){

        Iterator<String> keys = params.keySet().iterator();
        Iterator<String> values = params.values().iterator();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("?");

        for (int i=0;i<params.size();i++ ) {
            String value=null;
            try {
                value= URLEncoder.encode(values.next(),"utf-8");
            }catch (Exception e){
                e.printStackTrace();
            }

            stringBuffer.append(keys.next()+"="+value);
            if (i!=params.size()-1) {
                stringBuffer.append("&");
            }
            Log.v("stringBuffer",stringBuffer.toString());
        }

        return url + stringBuffer.toString();
    }


}
