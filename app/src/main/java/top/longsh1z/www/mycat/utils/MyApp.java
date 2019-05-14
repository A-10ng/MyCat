package top.longsh1z.www.mycat.utils;

import android.app.Application;

public class MyApp extends Application {
    public static final String SERVER_URL = "http://120.78.219.119:8080/MyCatServer/";
    private static String phone;
    private static String username;
    private static int gender;
    private static String catId;

    @Override
    public void onCreate() {
        super.onCreate();
        setCurUserPhone("12345678910");
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

    public static String getCurUserCatId() {
        return catId;
    }

    public static void setCurUserCatId(String catId) {
        MyApp.catId = catId;
    }
}
