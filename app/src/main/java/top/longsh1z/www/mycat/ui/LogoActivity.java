package top.longsh1z.www.mycat.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.utils.ActivityCollector;
import top.longsh1z.www.mycat.utils.MyApp;

public class LogoActivity extends AppCompatActivity {

    private LinearLayout ll_logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        ActivityCollector.addActivity(this);

        ll_logo = findViewById(R.id.ll_logo);

        AlphaAnimation animation = new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(2000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //获取旧token，验证token是否正确
                SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
                String tokenold = sp.getString("token", null);
                Log.i("LogoActivity", "onAnimationEnd: "+tokenold);
                if (tokenold != null) {
                    //get 验证token是否正确
                    OkHttpClient mOkHttpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://120.78.219.119:8080/MyCatServer/tokenVerify")
                            .addHeader("token", tokenold)
                            .build();
                    Call call = mOkHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String str = response.body().string();
                            switch (str) {
                                case "0": {  //token失效
                                    finish();
                                    Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    break;
                                }
                                case "1": {  //验证成功
                                    /**
                                     * 跳到主页面
                                     */
                                    SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
                                    String phoneNum = sharedPreferences.getString("phoneNumber", null);
                                    MyApp.setCurUserPhone(phoneNum);
                                    finish();
                                    /**
                                     * 这里加一个进度条
                                     */
                                    Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    break;
                                }
                                case "2": {  //token错误
                                    Toast toast = Toast.makeText(LogoActivity.this, "token错误！", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    finish();
                                    Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    break;
                                }
                            }
                        }
                    });
                }else {
                    finish();
                    Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ll_logo.setAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
