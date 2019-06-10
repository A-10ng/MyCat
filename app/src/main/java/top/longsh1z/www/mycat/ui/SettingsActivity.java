package top.longsh1z.www.mycat.ui;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.bean.Cat;
import top.longsh1z.www.mycat.bean.User;
import top.longsh1z.www.mycat.customview.MyToolBar;
import top.longsh1z.www.mycat.utils.ActivityCollector;
import top.longsh1z.www.mycat.utils.HttpUtils;
import top.longsh1z.www.mycat.utils.MyApp;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout rl_changeUsername;
    private RelativeLayout rl_changeGender;
    private RelativeLayout rl_changeCatName;
    private Button btn_exit;
    private ImageView iv_return;

    private Toolbar toolbar;

    private User CurUser;
    private Cat CurCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActivityCollector.addActivity(this);

        //根据手机号获取catId
        getUGC();

        initViews();

        setViewsOnClickListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initViews() {
        rl_changeUsername = findViewById(R.id.rl_changeUsername);
        rl_changeGender = findViewById(R.id.rl_changeGender);
        rl_changeCatName = findViewById(R.id.rl_changeCatName);
        btn_exit = findViewById(R.id.btn_exit);
        iv_return = findViewById(R.id.iv_return);
    }

    private void setViewsOnClickListener() {
        rl_changeUsername.setOnClickListener(this);
        rl_changeGender.setOnClickListener(this);
        rl_changeCatName.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        iv_return.setOnClickListener(this);
    }

    private void getUGC() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CurUser = HttpUtils.getCurUser();
                    MyApp.setCatId(CurUser.getCatId());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(SettingsActivity.this, "获取数据失败...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                finish();
                break;
            case R.id.btn_exit:
                ActivityCollector.finishAllActivity();
                startActivity(new Intent(SettingsActivity.this,LoginActivity.class));
                break;
            case R.id.rl_changeUsername:
                Log.i("SettingsActivity", "onClick: rl_changeUsername");
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                View view = LayoutInflater.from(this).inflate(R.layout.btn_add_dialog_layout, null, false);

                TextView tips = view.findViewById(R.id.textView);
                tips.setText("修改昵称");
                EditText editText = view.findViewById(R.id.et_checkContent);
                editText.setHint("请输入要修改的昵称");

                Button btn_ensure = view.findViewById(R.id.btn_ensure);
                Button btn_cancel = view.findViewById(R.id.btn_cancel);
                btn_ensure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("SettingsActivity", "onClick: ensure");
                                String usrname = editText.getText().toString().trim();
                                Log.i("SettingsActivity", "usrname"+usrname);
                                Looper.prepare();
                                try {
                                    if (!TextUtils.isEmpty(usrname)) {
                                        boolean res = HttpUtils.updateUsername(usrname);
                                        Log.i("SettingsActivity", "res"+res);
                                        if (res == true){
                                            Toast toast = Toast.makeText(SettingsActivity.this, "修改成功！", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            dialog.dismiss();
                                        }else{
                                            Toast toast = Toast.makeText(SettingsActivity.this, "修改失败！", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            dialog.dismiss();
                                        }
                                    } else {
                                        Toast toast = Toast.makeText(SettingsActivity.this, "请输入要修改的昵称哦。", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast toast = Toast.makeText(SettingsActivity.this, "断网了...", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                                Looper.loop();
                            }
                        }).start();
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("MainActivity", "onClick: cancel");
                        dialog.dismiss();
                    }
                });
                dialog.setView(view);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                //设置背景透明
                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                break;

            case R.id.rl_changeGender:
                Log.i("SettingsActivity", "onClick: rl_changeGender");
                AlertDialog dialog1 = new AlertDialog.Builder(this).create();
                View view1 = LayoutInflater.from(this).inflate(R.layout.btn_changegender_dialog, null, false);

                Button btn_ensure1 = view1.findViewById(R.id.btn_ensure);
                Button btn_cancel1 = view1.findViewById(R.id.btn_cancel);
                btn_ensure1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("SettingsActivity", "onClick: ensure");
                                RadioGroup radioGroup = view1.findViewById(R.id.radioGroup);
                                String gender = radioGroup.getCheckedRadioButtonId() == R.id.male ? "1" : "0";
                                Log.i("SettingsActivity", "gender: "+gender);
                                Looper.prepare();
                                try {
                                        boolean res = HttpUtils.updateGender(gender);
                                        Log.i("SettingsActivity", "res "+res);
                                        if (res == true){
                                            Toast toast = Toast.makeText(SettingsActivity.this, "修改成功！", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            dialog1.dismiss();
                                        }else{
                                            Toast toast = Toast.makeText(SettingsActivity.this, "修改失败！", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            dialog1.dismiss();
                                        }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast toast = Toast.makeText(SettingsActivity.this, "断网了...", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                                Looper.loop();
                            }
                        }).start();
                    }
                });
                btn_cancel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("SettingsActivity", "onClick: cancel");
                        dialog1.dismiss();
                    }
                });
                dialog1.setView(view1);
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();
                //设置背景透明
                dialog1.getWindow().setBackgroundDrawableResource(R.color.transparent);
                break;

            case R.id.rl_changeCatName:
                Log.i("SettingsActivity", "onClick: rl_changeCatName");
                AlertDialog dialog2 = new AlertDialog.Builder(this).create();
                View view2 = LayoutInflater.from(this).inflate(R.layout.btn_add_dialog_layout, null, false);

                TextView tips2 = view2.findViewById(R.id.textView);
                tips2.setText("修改猫的昵称");
                EditText editText2 = view2.findViewById(R.id.et_checkContent);
                editText2.setHint("请输入猫的昵称");

                Button btn_ensure2 = view2.findViewById(R.id.btn_ensure);
                Button btn_cancel2 = view2.findViewById(R.id.btn_cancel);
                btn_ensure2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("SettingsActivity", "onClick: ensure");
                                String catName = editText2.getText().toString().trim();
                                Log.i("SettingsActivity", "catName "+catName);
                                Looper.prepare();
                                try {
                                    if (!TextUtils.isEmpty(catName)) {
                                        boolean res = HttpUtils.updateCatName(catName);
                                        Log.i("SettingsActivity", "res"+res);
                                        if (res == true){
                                            Toast toast = Toast.makeText(SettingsActivity.this, "修改成功！", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            dialog2.dismiss();
                                        }else{
                                            Toast toast = Toast.makeText(SettingsActivity.this, "修改失败！", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            dialog2.dismiss();
                                        }
                                    } else {
                                        Toast toast = Toast.makeText(SettingsActivity.this, "请输入要猫的昵称哦。", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast toast = Toast.makeText(SettingsActivity.this, "断网了...", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                                Looper.loop();
                            }
                        }).start();
                    }
                });
                btn_cancel2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("SettingsActivity", "onClick: cancel");
                        dialog2.dismiss();
                    }
                });
                dialog2.setView(view2);
                dialog2.setCanceledOnTouchOutside(false);
                dialog2.show();
                //设置背景透明
                dialog2.getWindow().setBackgroundDrawableResource(R.color.transparent);
                break;
        }
    }
}
