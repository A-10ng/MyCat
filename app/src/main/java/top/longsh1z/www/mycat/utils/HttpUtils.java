package top.longsh1z.www.mycat.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.longsh1z.www.mycat.model.Check;

public class HttpUtils {

    private static OkHttpClient client = new OkHttpClient();

    public static void sendHttpRequest(String address, Callback callback){
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static List<Check> getCheckInfo() {
        Request request = new Request.Builder()
                .url(MyApp.SERVER_URL+"findCheck?phone="+MyApp.getCurUserPhone())
                .build();
        try {
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            List<Check> CheckList = getList(data,new TypeToken<List<Check>>() {}.getType());
            return CheckList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Check> getList(String data, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(data,type);
    }

    public static boolean isInsertSuccess(String s) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("phone",MyApp.getCurUserPhone())
                .add("checkItem",s)
                .build();
        Request request = new Request.Builder()
                .url(MyApp.SERVER_URL+"addCheckItem")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String num = response.body().string().trim();
        Log.i("this", "isInsertSuccess:string "+num);
        if (num == "1" || num.equals("1")){
            Log.i("this", "isInsertSuccess:true ");
            return true;
        }else{
            Log.i("this", "isInsertSuccess:false ");
            return false;
        }
    }
}
