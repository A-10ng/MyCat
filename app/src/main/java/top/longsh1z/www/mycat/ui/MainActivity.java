package top.longsh1z.www.mycat.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.customview.CatFoodView;
import top.longsh1z.www.mycat.customview.UnfoldButton;
import top.longsh1z.www.mycat.model.CatFood;
import top.longsh1z.www.mycat.model.Check;
import top.longsh1z.www.mycat.utils.HttpUtils;

public class MainActivity extends AppCompatActivity {

    private TextView tv_username;
    private TextView tv_value;
    private Button btn_statistics;
    private Button btn_add;
    private Button btn_setup;
    private RelativeLayout relativeLayout;
    private SmartRefreshLayout mSmartRefreshLayout;
    private String TAG = "MainActivity>>>>>";
    //当前打卡项动画可选的X轴的位置，一个默认在中间，两个默认对称，三个默认为该list的前三个，四个默认为list的后四个，五个默认为全部
    private List<Float> CUR_CAN_CHOOSE_LIST_X;
    //Y轴的设置情况参考X轴的
    private List<Float> CUR_CAN_CHOOSE_LIST_Y = Arrays.asList((float) 360, (float) 410, (float) 410, (float) 460, (float) 460);
    private List<Integer> CUR_CAN_CHOOSE_LIST_ANIM_DURATION = Arrays.asList(2400, 3000, 3500, 3900, 2800);
    //当前打卡项可选的背景图片
    private List<Integer> CUR_CAN_CHOOSE_LIST_PIC = Arrays.asList(
            R.drawable.catpot, R.drawable.catfood, R.drawable.catmilk,
            R.drawable.cheese, R.drawable.fish);

    //当前已有的打卡项
    private List<Check> checkList;

    //当前已有的打卡项view
    private Map<Integer, View> checkViews = new HashMap<>();

    private static void setUpAnimation(final View catFoodView, long time) {
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, -30);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int curValue = (int) animation.getAnimatedValue();
//                catFoodView.layout(catFoodView.getLeft(), curValue, catFoodView.getRight(), curValue + catFoodView.getHeight());
//                //catFoodView.setTranslationY(curValue);
//                //catFoodView.setY(curValue+catFoodView.getBottom());
//            }
//        });
//        valueAnimator.setDuration(time);
//        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        valueAnimator.start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(catFoodView, "translationY",
                catFoodView.getY() + 0, catFoodView.getY() - 30);
        objectAnimator.setDuration(time);
        objectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();
    }

    private static void setDownAnimation(final View catFoodView, long time) {
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 30);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int curValue = (int) animation.getAnimatedValue();
//                catFoodView.layout(catFoodView.getLeft(), curValue, catFoodView.getRight(), curValue + catFoodView.getHeight());
//                //catFoodView.setTranslationY((float)curValue);
//            }
//        });
//        valueAnimator.setDuration(time);
//        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        valueAnimator.start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(catFoodView, "translationY",
                catFoodView.getY() + 0, catFoodView.getY() + 30);
        objectAnimator.setDuration(time);
        objectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        initViews();

        setViewOnClickListeners();

        //初始化打卡项的动画
        initCheckView();

        //获取东八区（即北京）的当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MM dd HH mm ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Log.i(TAG, "onCreate: " + simpleDateFormat.format(new Date()));

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initCheckView();
                mSmartRefreshLayout.finishRefresh();
            }
        });
    }

    private void initViews() {
        tv_username = findViewById(R.id.tv_username);
        tv_value = findViewById(R.id.tv_value);
        btn_statistics = findViewById(R.id.btn_statistics);
        btn_add = findViewById(R.id.btn_add);
        btn_setup = findViewById(R.id.btn_setup);
        relativeLayout = findViewById(R.id.RL);
        mSmartRefreshLayout = findViewById(R.id.refresh_layout);

        initXList();
        initYList();
    }

    private void setViewOnClickListeners() {
        btn_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SQLActivity.class));
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
                Log.i("MainActivity", "onClick: 添加");
            }
        });
        btn_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initCheckView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkList != null) {
                    checkList.clear();
                    //清空已有的打卡项动画
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Integer key : checkViews.keySet()) {
                                relativeLayout.removeView(checkViews.get(key));
                            }
                        }
                    });
                }
                checkList = HttpUtils.getCheckInfo();
                int checkListSize = checkList.size();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (checkListSize != 0) {
                            //生成每一个具体的打卡项浮动动画
                            // 一个默认在中间
                            if (checkListSize == 1) {
                                createCheckView(0, checkList.get(0).getCheckItem());
                            } else if (checkListSize == 2) {
                                for (int i = 0; i < 2; i++) {
                                    createCheckView(i + 1, checkList.get(i).getCheckItem());
                                }
                            } else if (checkListSize == 3) {
                                for (int i = 0; i < 3; i++) {
                                    createCheckView(i, checkList.get(i).getCheckItem());
                                }
                            } else if (checkListSize == 4) {
                                for (int i = 0; i < 4; i++) {
                                    createCheckView(i + 1, checkList.get(i).getCheckItem());
                                }
                            } else {
                                for (int i = 0; i < 5; i++) {
                                    createCheckView(i, checkList.get(i).getCheckItem());
                                }
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void initXList() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        float a = (float) ((int) width * 0.45);
        float b = (float) ((int) width * 0.3);
        float c = (float) ((int) width * 0.6);
        float d = (float) ((int) width * 0.15);
        float e = (float) ((int) width * 0.75);
        CUR_CAN_CHOOSE_LIST_X = Arrays.asList(a, b, c, d, e);
    }

    private void initYList() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int height = outMetrics.heightPixels;
        float a = (float) ((int) height * 0.2);
        float b = (float) ((int) height * 0.22);
        float c = (float) ((int) height * 0.22);
        float d = (float) ((int) height * 0.24);
        float e = (float) ((int) height * 0.24);
        CUR_CAN_CHOOSE_LIST_Y = Arrays.asList(a, b, c, d, e);
    }

    private void createCheckView(int position, String checkItem) {
        CatFood catFood = new CatFood(checkItem, 50);
        View catFoodView = LayoutInflater.from(this).inflate(R.layout.item_catfood, null);
        LinearLayout linearLayout = catFoodView.findViewById(R.id.linearLayout);
        linearLayout.setClickable(true);
        catFoodView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: view" + catFood.getType());
            }
        });
        TextView tv_water = catFoodView.findViewById(R.id.tv_water);
        Button btn_bg = catFoodView.findViewById(R.id.btn_bg);
        btn_bg.setBackgroundResource(CUR_CAN_CHOOSE_LIST_PIC.get(position));
        tv_water.setText(catFood.getType() + ":" + catFood.getNumber() + "g");
        relativeLayout.addView(catFoodView);
//        btn_bg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "onClick:btn "+catFood.getType());
//            }
//        });
        catFoodView.setX(CUR_CAN_CHOOSE_LIST_X.get(position));
        catFoodView.setY(CUR_CAN_CHOOSE_LIST_Y.get(position));
        catFoodView.setAlpha(0);
        catFoodView.setScaleX(0);
        catFoodView.setScaleY(0);
        catFoodView.animate().alpha(1).scaleX(1).scaleY(1).setDuration(500).start();
        if (new Random().nextInt(2) == 0) {
            setUpAnimation(catFoodView, CUR_CAN_CHOOSE_LIST_ANIM_DURATION.get(position));
        } else {
            setDownAnimation(catFoodView, CUR_CAN_CHOOSE_LIST_ANIM_DURATION.get(position));
        }
        checkViews.put(position, catFoodView);
    }

    private void showAddDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.btn_add_dialog_layout, null, false);
        Button btn_ensure = view.findViewById(R.id.btn_ensure);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("MainActivity", "onClick: ensure");
                        EditText et_checkContent = view.findViewById(R.id.et_checkContent);
                        String checkContent = et_checkContent.getText().toString().trim();
                        Looper.prepare();
                        try {
                            if (!TextUtils.isEmpty(checkContent)) {
                                Log.i("MainActivity", "onClick: CHECK");
                                if (checkList.size() != 5) {
                                    Log.i("MainActivity", "onClick: " + checkList.size());
                                    if (HttpUtils.isInsertSuccess(checkContent)) {
                                        Log.i("MainActivity", "onClick: isInsertSuccess");
                                        initCheckView();
                                        dialog.dismiss();
                                    } else {
                                        Toast toast = Toast.makeText(MainActivity.this, "新建打卡项出现异常！", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                } else {
                                    Toast toast = Toast.makeText(MainActivity.this, "当前打卡项数量已达上限！", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            } else {
                                Toast toast = Toast.makeText(MainActivity.this, "请输入打卡项哦。", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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
//                dialog.getWindow().setDimAmount(0f);
    }
}
