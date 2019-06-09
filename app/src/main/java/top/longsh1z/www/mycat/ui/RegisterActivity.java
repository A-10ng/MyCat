package top.longsh1z.www.mycat.ui;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import top.longsh1z.www.mycat.R;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.longsh1z.www.mycat.utils.MyApp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup radiogroup;
    private RadioButton radiobutton;
    private EditText catname;
    private EditText username;
    private Spinner spinner;
    private ArrayAdapter adapter;
    private Button entrance;
    private String Type = "布偶猫";
    private String phone = MyApp.getCurUserPhone();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        radiogroup = (RadioGroup) findViewById(R.id.catselect);
        catname = findViewById(R.id.catname_input);
        spinner = (Spinner) findViewById(R.id.spinner_genderselect);
        username = (EditText) findViewById(R.id.username_input);
        entrance = (Button) findViewById(R.id.entrance);
        entrance.setOnClickListener(this);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectRadioBtn();
            }
        });

        //spinner new Adapter对象
        adapter = new ArrayAdapter<String>(this, R.layout.spinneritem, getDataSource()); //getDataSource为数组名
        adapter.setDropDownViewResource(R.layout.drown);
        spinner.setAdapter(adapter);
    }

    private void selectRadioBtn() {
        radiobutton = (RadioButton) findViewById(radiogroup.getCheckedRadioButtonId());
        Type = radiobutton.getText().toString();
    }

    //用于spinner的数组
    public List<String> getDataSource() {
        List<String> list = new ArrayList<String>();
        list.add("男");
        list.add("女");
        return list;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.entrance:  //单击进入MyCat按钮
                if (TextUtils.isEmpty(catname.getText().toString().trim())) {
                    Toast toast = Toast.makeText(RegisterActivity.this, "猫猫没有名字噢，请给猫猫起个名字~", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (TextUtils.isEmpty(username.getText().toString().trim())) {
                    Toast toast = Toast.makeText(RegisterActivity.this, "请输入用户名~", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Entrance();
                }
                break;
        }
    }

    public void Entrance() {
        //获取要传的参数值
        String name = username.getText().toString();
        String catName = catname.getText().toString();
        String gender = null;
        switch (spinner.getSelectedItem().toString()) {
            case "男":
                gender = "1";
                break;
            case "女":
                gender = "0";
                break;
        }

        //POST参数phone, username, gender, catId, type
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("phone", phone)
                .add("username", name)
                .add("gender", gender)
                .add("catId", catName)
                .add("type", Type)
                .build();
        final Request request = new Request.Builder()
                .url("http://120.78.219.119:8080/MyCatServer/register")
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("message", "failure");
                Toast toast = Toast.makeText(RegisterActivity.this, "注册失败了~", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                final String str = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("messege-respone", str);
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }
        });
    }
}
