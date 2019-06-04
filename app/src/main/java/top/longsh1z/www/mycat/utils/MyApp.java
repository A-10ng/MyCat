package top.longsh1z.www.mycat.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MyApp extends Application {
    public static final String SERVER_URL = "http://120.78.219.119:8080/MyCatServer/";
    private static final String TAG = "MyApp";
    private static String phone;
    private static String username;
    private static int gender;
    private static String catId;
    private static Context mContext;
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

    public static String getCurUserCatId() {
        return catId;
    }

    public static void setCurUserCatId(String catId) {
        MyApp.catId = catId;
    }

    public static String getCurTime() {
        //获取东八区（即北京）的当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Log.i(TAG, simpleDateFormat.format(new Date()));
        return simpleDateFormat.format(new Date());

    }

    @Override
    public void onCreate() {
        super.onCreate();
        setCurUserPhone("12345678910");
        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
