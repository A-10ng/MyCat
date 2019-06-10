package top.longsh1z.www.mycat.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.longsh1z.www.mycat.ui.LoginActivity;
import top.longsh1z.www.mycat.ui.MainActivity;

public class MyApp extends Application {
    public static final String SERVER_URL = "http://120.78.219.119:8080/MyCatServer/";
    private static final String TAG = "MyApp";
    private static String phone;
    private static String username;
    private static int gender;
    private static String catId;
    private static String catName;
    private static Context mContext;

    public static String getCatId() {
        return catId;
    }

    public static void setCatId(String catId) {
        MyApp.catId = catId;
    }

    public static String getCurUserPhone() {
        return phone;
    }

    public static void setCurUserPhone(String phone) {
        MyApp.phone = phone;
    }

    public static String getCurUserUsername() {
        return username;
    }

    public static void setCurUserUsername(String username) {
        MyApp.username = username;
    }

    public static int getCurUserGender() {
        return gender;
    }

    public static void setCurUserGender(int gender) {
        MyApp.gender = gender;
    }

    public static String getCurUserCatName() {
        return catName;
    }

    public static void setCurUserCatName(String catName) {
        MyApp.catName = catName;
    }

    public static String getCurTime() {
        //获取东八区（即北京）的当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Log.i(TAG, simpleDateFormat.format(new Date()));
        return simpleDateFormat.format(new Date());

    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
