package top.longsh1z.www.mycat.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.PrimitiveIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.utils.ActivityCollector;
import top.longsh1z.www.mycat.utils.MyApp;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private long mExitTime;
    private TextInputLayout phoneNum;
    private TextInputLayout veri;
    private Button button;
    private Button veri_button;
    private TimeCount time;// 60S计时
    private String tokenold = null;//获取本地旧的token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCollector.addActivity(this);
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView() {
        phoneNum = findViewById(R.id.phoneNum);
        veri = findViewById(R.id.Verification);
        veri_button = (Button) findViewById(R.id.veri_get);
        button = (Button) findViewById(R.id.login);
        veri_button.setOnClickListener(this);
        button.setOnClickListener(this);

//        //获取旧token，验证token是否正确
//        SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
//        tokenold = sp.getString("token", null);
//        if (tokenold != null) {
//            //get 验证token是否正确
//            OkHttpClient mOkHttpClient = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url("http://120.78.219.119:8080/MyCatServer/tokenVerify")
//                    .addHeader("token", tokenold)
//                    .build();
//            Call call = mOkHttpClient.newCall(request);
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    final String str = response.body().string();
//                    switch (str) {
//                        case "0": {  //token失效
//
//                            break;
//                        }
//                        case "1": {  //验证成功
//                            /**
//                             * 跳到主页面
//                             */
//                            SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
//                            String phoneNum = sharedPreferences.getString("phoneNumber", null);
//                            MyApp.setCurUserPhone(phoneNum);
//                            finish();
//                            /**
//                             * 这里加一个进度条
//                             */
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            break;
//                        }
//                        case "2": {  //token错误
//
//                            break;
//                        }
//                    }
//
//                }
//            });
//
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login: //单击登录按钮
                if (phoneISTrue() == 1) {
                    submit(phoneNum.getEditText().getText().toString().trim(), veri.getEditText().getText().toString().trim());
                }
                break;
            case R.id.veri_get: //单击获取验证码按钮
                if (phoneISTrue() == 1) {
                    getVeri(phoneNum.getEditText().getText().toString().trim());
                }
                break;
        }
    }

    //判断手机号是否正确，返回：0 手机号错误，1 手机号正确
    public int phoneISTrue() {
        phoneNum.setErrorEnabled(true);
        if (phoneNum.getEditText().getText().length() == 0 || phoneNum.getEditText().getText().length() > 11 || phoneNum.getEditText().getText().length() < 11) {
            phoneNum.setError("手机号字数不符合要求");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    phoneNum.setErrorEnabled(false);
                }
            }, 3000); // 延时3秒
            return 0;//表示手机号错误
        } else {
            phoneNum.setErrorEnabled(false);
            return 1;//手机号正确
        }
    }

    //登录提交
    public void submit(String phoneNumber, String code) {

        //post 判断验证码是否正确
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("phone", phoneNumber)
                .add("code", code)
                .build();
        final Request request = new Request.Builder()
                .url("http://120.78.219.119:8080/MyCatServer/login")
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("message", "failure");
                Toast toast = Toast.makeText(LoginActivity.this, "登录失败~", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                final String str = response.body().string();
                final Headers responseHeader = response.headers();
                Log.i("token11 ", responseHeader.get("token"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("messege", str);
                        switch (str) {
                            case "0": {  //手机号错误
                                Toast toast = Toast.makeText(LoginActivity.this, "请先获取验证码！", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                break;
                            }
                            case "1": {
                                //获取token并保存本地
                                SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
                                sp.edit()
                                        .putString("token", responseHeader.get("token"))
                                        .putString("phoneNumber", phoneNumber)
                                        .apply();
                                Log.i("token保存成功  ", responseHeader.get("token"));
                                //跳转注册
                                MyApp.setCurUserPhone(phoneNumber);
                                Toast.makeText(LoginActivity.this, "欢迎来到MyCat的世界，先完善您的信息吧~", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            }
                            case "2": {  //老用户
                                SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
                                sp.edit()
                                        .putString("token", responseHeader.get("token"))
                                        .putString("phoneNumber", phoneNumber)
                                        .apply();
                                /**
                                 * 这里要写登录的页面
                                 */
                                MyApp.setCurUserPhone(phoneNumber);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                                break;
                            }
                            case "3": {  //验证码过期
                                Toast toast = Toast.makeText(LoginActivity.this, "验证码已过期,请重新获取噢~", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                break;
                            }
                            case "4": {  //验证码错误
                                Toast toast = Toast.makeText(LoginActivity.this, "验证码错误,请重新输入~", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                break;
                            }
                        }
                    }
                });
            }
        });

        String number = phoneNum.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(LoginActivity.this, "请输入手机号", Toast.LENGTH_LONG).show();
        }
    }

    //获取验证码
    private void getVeri(String phoneNumber) {
        //60S倒计时
        time = new TimeCount(60000, 1000);
        time.start();

        //post获取验证码
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("phone", phoneNumber)
                .build();
        Request request = new Request.Builder()
                .url("http://120.78.219.119:8080/MyCatServer/getCode")
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(LoginActivity.this, "服务器连接失败~", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (str) {
                            case "1": {  //成功
                                Toast.makeText(LoginActivity.this, "验证码已发送~", Toast.LENGTH_LONG).show();
                                break;
                            }
                            case "2": {  //服务器错误
                                Toast.makeText(LoginActivity.this, "服务器错误，研发小哥哥正在努力维护，请稍等一会再尝试", Toast.LENGTH_LONG).show();
                                break;
                            }
                            case "3": {  //手机号码错误
                                Toast.makeText(LoginActivity.this, "该手机号不存在噢", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }

                    }
                });
            }
        });
    }

    //60s验证码倒计时
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            veri_button.setClickable(false);
            veri_button.setText("(" + millisUntilFinished / 1000 + ") 秒后可重发");
        }

        @Override
        public void onFinish() {
            veri_button.setText("重新获取验证码");
            veri_button.setClickable(true);
        }
    }
}
