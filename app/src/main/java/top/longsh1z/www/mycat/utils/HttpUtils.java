package top.longsh1z.www.mycat.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.longsh1z.www.mycat.bean.MyCatWorldBean;
import top.longsh1z.www.mycat.bean.Check;

public class HttpUtils {

    private static OkHttpClient client = new OkHttpClient();
    private static String TAG = "HttpUtils";

//    public static void sendHttpRequest(String address, Callback callback){
//        Request request = new Request.Builder()
//                .url(address)
//                .build();
//        client.newCall(request).enqueue(callback);
//    }

    public static List<Check> getCheckInfo() {
        Request request = new Request.Builder()
                .url(MyApp.SERVER_URL + "findCheck?phone=" + MyApp.getCurUserPhone())
                .build();
        try {
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            List<Check> CheckList = getCheckList(data, new TypeToken<List<Check>>() {
            }.getType());
            return CheckList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Check> getCheckList(String data, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(data, type);
    }

    public static boolean isInsertSuccess(String s) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("phone", MyApp.getCurUserPhone())
                .add("checkItem", s)
                .build();
        Request request = new Request.Builder()
                .url(MyApp.SERVER_URL + "addCheckItem")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String num = response.body().string().trim();
        Log.i("this", "isInsertSuccess:string " + num);
        if (num == "1" || num.equals("1")) {
            Log.i("this", "isInsertSuccess:true ");
            return true;
        } else {
            Log.i("this", "isInsertSuccess:false ");
            return false;
        }
    }

    public static boolean cancelCheckItem(String checkItem) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("phone", MyApp.getCurUserPhone())
                .add("checkItem", checkItem)
                .build();
        Request request = new Request.Builder()
                .url(MyApp.SERVER_URL + "deleteCheckItem")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String res = response.body().string().trim();
        if (res == "1" || res.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean finishCheckItem(String checkItem, String date) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("phone", MyApp.getCurUserPhone())
                .add("checkItem", checkItem)
                .add("date", date)
                .build();
        Request request = new Request.Builder()
                .url(MyApp.SERVER_URL + "updateCheckItem")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String res = response.body().string().trim();
        if (res == "1" || res.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public static List<Check> getGrowthRecord() throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("phone", MyApp.getCurUserPhone())
                .add("state", "1")
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url(MyApp.SERVER_URL + "findCheck")
                .build();
        Response response = client.newCall(request).execute();
        String data = response.body().string();
        List<Check> growthRecordList = getCheckList(data, new TypeToken<List<Check>>() {
        }.getType());
        return growthRecordList;
    }

    public static List<MyCatWorldBean> getMyCatWorldData() throws IOException {
        Request request = new Request.Builder()
                .url(MyApp.SERVER_URL + "getUC")
                .build();
        Response response = client.newCall(request).execute();
        String data = response.body().string();
        List<MyCatWorldBean> myCatWorldList = getMyCatWorldList(data, new TypeToken<List<MyCatWorldBean>>() {}.getType());
        return myCatWorldList;
    }

    private static List<MyCatWorldBean> getMyCatWorldList(String data, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(data, type);
    }

    public static boolean haveFiveRecord() throws IOException, ParseException {
        List<Check> recordList = getGrowthRecord();
        String curMon = CalendarUtils.CurTimeInfor().get("month")+"";
        String curDate = CalendarUtils.CurTimeInfor().get("CurDate")+"";
        String curMonDate = curMon+"-"+curDate;
        Log.i(TAG, "haveFiveRecord: curMonDate"+curMonDate);
        int todayCheckNum = 0;

        for (int i = 0; i <recordList.size(); i++) {
            String checkMon = CalendarUtils.getCurMonthThroDate( recordList.get(i).getDate());
            int checkDate = CalendarUtils.getCurDateThroDate( recordList.get(i).getDate());
            String checkMonDate = checkMon+"-"+checkDate;

            if (curMonDate == checkMonDate || curMonDate.equals(checkMonDate)){
                todayCheckNum++;
            }
        }
        if (todayCheckNum<=4){
            return false;
        }else {
            return true;
        }
    }
}
