package top.longsh1z.www.mycat.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.model.Cat;
import top.longsh1z.www.mycat.model.Check;
import top.longsh1z.www.mycat.model.User;
import top.longsh1z.www.mycat.utils.DBUtils;
import top.longsh1z.www.mycat.utils.HttpUtils;
import top.longsh1z.www.mycat.utils.MyApp;

public class SQLActivity extends AppCompatActivity {

    private TextView textView;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql);
        textView = findViewById(R.id.textView);
        getUserInfo();
    }

    private void getUserInfo() {
        HttpUtils.sendHttpRequest(MyApp.SERVER_URL+"findCheck?phone=12345678910", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("fail", "onFailure:");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                data = response.body().string();
                Gson gson = new Gson();
                    List<Check> UserList = gson.fromJson(data, new TypeToken<List<Check>>() {
                }.getType());
                Log.i("GSON>>>>>", "phone: " + UserList.get(0).getCheckItem());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(data);
                    }
                });
            }
        });
    }
}
