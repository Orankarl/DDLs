package com.example.orankarl.ddls;

import android.provider.ContactsContract;
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
    private static String url = "http://ddl.strickerlee.tk";
    public static boolean isLogin = false;
    public static String token = "";
    public static OkHttpClient client;
    public static String username = "local", nickname, stuID;

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

    public static void logOut() {
        Net.token = "";
        Net.isLogin = false;
        Net.username = "local";
    }

    public static String register(String username, String stuNumber, String nickName, String password) {
        try {
            if (client == null) client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("username", username)
                    .add("stuid", stuNumber)
                    .add("password", password)
                    .add("nickname", nickName)
                    .build();
            Request request = new Request.Builder()
                    .url(url+"/api/user")
                    .put(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            String status = jsonObject.getString("status");
            String reason = jsonObject.getString("reason");
            if (status.equals("1")) {
                return "";
            } else {
                return reason;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No response";
    }

    public static String getInfo() {
        try {
            if (client == null) client = new OkHttpClient();

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("token", Net.token);
            Request request = new Request.Builder()
                    .url(attachHttpGetParams(url+"/api/user", params))
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
                String username = jsonObject.getString("username");
                String nickname = jsonObject.getString("nickname");
                String stuid = jsonObject.getString("stuid");
                Net.nickname = nickname;
                Net.username = username;
                Net.stuID = stuid;
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
            String string = response.body().string();
            Log.d("json", string);

            JSONObject jsonObject = new JSONObject(string);
            String status = jsonObject.getString("status");
            String reason = jsonObject.getString("reason");
            if (status.equals("-1")) return reason;
            else {
                JSONArray array = jsonObject.getJSONArray("deadlines");
                int len = array.length();
                List<Deadline> deadlineList = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    JSONObject json = array.getJSONObject(i);
                    deadlineList.add(new Deadline(json.getInt("id"),
                            json.getLong("time"), json.getString("title"),
                            json.getString("description"), Net.username, (json.getInt("done") != 0)));
                }
                int code = DatabaseManager.insertDeadlines(deadlineList);
                if (code == 0) return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No Response";
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
            String string = response.body().string();
            Log.d("Add Deadline", string);
            JSONObject jsonObject = new JSONObject(string);
            //parseObject
            String status = jsonObject.getString("status");
            String reason = jsonObject.getString("reason");
            if (status.equals("1")) {
                long id = jsonObject.getLong("id");
                return String.valueOf(id);
            } else {
                return reason;
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No response";
    }

    public static String queryNotices() {
        try {
            if (client == null) client = new OkHttpClient();

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("token", token);
            Request request = new Request.Builder()
                    .url(attachHttpGetParams(url+"/api/notice", params))
                    .build();
            Response response = client.newCall(request).execute();

            String string = response.body().string();
            Log.d("json", string);

            JSONObject jsonObject = new JSONObject(string);
            String status = jsonObject.getString("status");
            String reason = jsonObject.getString("reason");
            if (status.equals("-1")) return reason;
            else {
                JSONArray array = jsonObject.getJSONArray("notices");
                int len = array.length();
                List<Notice> noticeList = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    JSONObject json = array.getJSONObject(i);
                    noticeList.add(new Notice(json.getInt("id"),
                            json.getString("title"),
                            json.getString("course_name"),
                            json.getString("description"), Net.username));
                }
                DatabaseManager.deleteNotices();
                int code = DatabaseManager.insertNotices(noticeList);
                if (code == 0) return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No Response";
    }

    public static String finishDeadline(int id, boolean finished) {
        try {
            if (client == null) client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient();
            String done;
            if (finished) done = "1";
            else done = "0";
            RequestBody requestBody = new FormBody.Builder()
                    .add("token", token)
                    .add("done", done)
                    .build();
            Request request = new Request.Builder()
                    .url(url+"/api/deadline/"+String.valueOf(id))
                    .put(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            Log.d("Finish Deadline", string);
            JSONObject jsonObject = new JSONObject(string);
            //parseObject
            String status = jsonObject.getString("status");
            String reason = jsonObject.getString("reason");
            if (status.equals("1")) {
                return "";
            } else {
                return reason;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No Response";
    }

    public static String deleteDeadline(int id) {
        try {
            if (client == null) client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("token", token)
                    .build();
            Request request = new Request.Builder()
                    .url(url+"/api/deadline/"+String.valueOf(id))
                    .delete(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            Log.d("Finish Deadline", string);
            JSONObject jsonObject = new JSONObject(string);
            //parseObject
            String status = jsonObject.getString("status");
            String reason = jsonObject.getString("reason");
            if (status.equals("1")) {
                return "";
            } else {
                return reason;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No Response";
    }

    public static String queryCourse() {
        try {
            if (client == null) client = new OkHttpClient();

            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("token", token);
            Request request = new Request.Builder()
                    .url(attachHttpGetParams(url+"/api/course", params))
                    .build();
            Response response = client.newCall(request).execute();

            String string = response.body().string();
            Log.d("json", string);

            JSONObject jsonObject = new JSONObject(string);
            String status = jsonObject.getString("status");
            String reason = jsonObject.getString("reason");
            if (status.equals("-1")) return reason;
            else {
                JSONArray array = jsonObject.getJSONArray("courses");
                int len = array.length();
                List<Course> courseList = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    JSONObject json = array.getJSONObject(i);
                    courseList.add(new Course(json.getInt("id"), json.getString("name"), json.getString("semester"), Net.username));
                }
                DatabaseManager.deleteNotices();
                int code = DatabaseManager.insertCourses(courseList);
                if (code == 0) return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No Response";
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
