package top.longsh1z.www.mycat.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.model.CatFood;
import top.longsh1z.www.mycat.model.Check;
import top.longsh1z.www.mycat.utils.CalendarUtils;
import top.longsh1z.www.mycat.utils.HttpUtils;
import top.longsh1z.www.mycat.utils.MyApp;

public class MainActivity extends AppCompatActivity {

    /**
     *猫咪动画模块
     */
    private TextView tv_username, tv_value;
    private Button btn_statistics, btn_add, btn_setup;
    private RelativeLayout relativeLayout;
    private SmartRefreshLayout mSmartRefreshLayout;
    private String TAG = "MainActivity>>>>>";
    //当前打卡项动画可选的X轴的位置，一个默认在中间，两个默认对称，三个默认为该list的前三个，四个默认为list的后四个，五个默认为全部
    private List<Float> CUR_CAN_CHOOSE_LIST_X;
    //Y轴的设置情况参考X轴的
    private List<Float> CUR_CAN_CHOOSE_LIST_Y;
    private List<Integer> CUR_CAN_CHOOSE_LIST_ANIM_DURATION = Arrays.asList(2400, 3000, 3500, 3900, 2800);
    //当前打卡项可选的背景图片
    private List<Integer> CUR_CAN_CHOOSE_LIST_PIC = Arrays.asList(
            R.drawable.catpot, R.drawable.catfood, R.drawable.catmilk,
            R.drawable.cheese, R.drawable.fish);

    //当前已有的打卡项
    private List<Check> checkList;

    //当前已有的打卡项view
    private Map<Integer, View> checkViews = new HashMap<>();

    //已经展开的打卡项
    private LinearLayout expandMiddleLayout;
    private LinearLayout expandBottomLayout;
    //已经展开的打卡项的view
    private View expandView;

    /**
     *成长记录模块
     */
    private TextView tv_checkRecord;
    private LinearLayout ll_checkMoreRecord;

    //成长记录根布局
    private LinearLayout ll_growthRecord;

    //当前已打卡的check集合
    private List<Check> growthRecordList;

    //当前已打卡的成长记录View集合
    private Map<Integer,View> growthRecordViewList = new HashMap<>();

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
        catFoodView.setTag(objectAnimator);
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
        catFoodView.setTag(objectAnimator);
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

        //初始化成长记录模块
        initGrowthRecord();

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initCheckView();
                initGrowthRecord();
                mSmartRefreshLayout.finishRefresh();
            }
        });
    }

    private void initGrowthRecord() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    growthRecordList = HttpUtils.getGrowthRecord();
                    int gRListSize = growthRecordList.size();
                    if (gRListSize != 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (growthRecordViewList != null){
                                    for (int key : growthRecordViewList.keySet()){
                                        ll_growthRecord.removeView(growthRecordViewList.get(key));
                                    }
                                }

                                for (int i = 0; i < 3; i++) {
                                    RelativeLayout relativeLayout = new RelativeLayout(MainActivity.this);
                                    RelativeLayout.LayoutParams rl_paras = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    );
                                    rl_paras.setMargins(20,15,20,15);
                                    relativeLayout.setLayoutParams(rl_paras);

                                    //动态生成左边的记录内容
                                    TextView left_textView = new TextView(MainActivity.this);
                                    left_textView.setEllipsize(TextUtils.TruncateAt.END);
                                    left_textView.setTextSize(15);
                                    left_textView.setTextColor(Color.BLACK);
                                    left_textView.setText("完成" + growthRecordList.get(i).getCheckItem() + "，喂养猫粮50g");
                                    RelativeLayout.LayoutParams rl_left_tvParas = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                    rl_left_tvParas.addRule(RelativeLayout.ALIGN_LEFT);
                                    left_textView.setLayoutParams(rl_left_tvParas);
                                    relativeLayout.addView(left_textView);

                                    /*
                                     *动态生成右边的记录时间
                                     */
                                    TextView right_textView = new TextView(MainActivity.this);
                                    right_textView.setEllipsize(TextUtils.TruncateAt.END);
                                    right_textView.setTextSize(15);
                                    try {
                                        right_textView.setText(CalendarUtils.getInternal(growthRecordList.get(i).getDate()));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    RelativeLayout.LayoutParams rl_right_tvParas = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                    rl_right_tvParas.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    right_textView.setLayoutParams(rl_right_tvParas);
                                    relativeLayout.addView(right_textView);

                                    ll_growthRecord.addView(relativeLayout,i);
                                    growthRecordViewList.put(i,relativeLayout);
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initViews() {
        //猫咪动画模块
        tv_username = findViewById(R.id.tv_username);
        tv_value = findViewById(R.id.tv_value);
        btn_statistics = findViewById(R.id.btn_statistics);
        btn_add = findViewById(R.id.btn_add);
        btn_setup = findViewById(R.id.btn_setup);
        relativeLayout = findViewById(R.id.RL);
        mSmartRefreshLayout = findViewById(R.id.refresh_layout);

        //成长记录模块
        ll_growthRecord = findViewById(R.id.ll_growthRecord);
        tv_checkRecord = findViewById(R.id.tv_checkRecord);
        ll_checkMoreRecord = findViewById(R.id.ll_checkMoreRecord);

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

        //监听查看更多记录
        ll_checkMoreRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MoreRecordActivity.class));
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
        float a = (float) ((int) width * 0.32);
        float b = (float) ((int) width * 0.17);
        float c = (float) ((int) width * 0.47);
        float d = (float) ((int) width * 0.00);
        float e = (float) ((int) width * 0.62);
        CUR_CAN_CHOOSE_LIST_X = Arrays.asList(a, b, c, d, e);
    }

    private void initYList() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int height = outMetrics.heightPixels;
        float a = (float) ((int) height * 0.08);
        float b = (float) ((int) height * 0.1);
        float c = (float) ((int) height * 0.1);
        float d = (float) ((int) height * 0.12);
        float e = (float) ((int) height * 0.12);
        CUR_CAN_CHOOSE_LIST_Y = Arrays.asList(a, b, c, d, e);
    }

    private void createCheckView(int position, String checkItem) {
        CatFood catFood = new CatFood(checkItem, 50);
        View catFoodView = LayoutInflater.from(this).inflate(R.layout.item_catfood, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                400, 400);
        catFoodView.setLayoutParams(params);

        //top层View
        LinearLayout linearLayout_top = catFoodView.findViewById(R.id.linearLayout_top);
        linearLayout_top.setClickable(true);
        TextView tv_water_top = catFoodView.findViewById(R.id.tv_water_top);
        TextView tv_bg_top = catFoodView.findViewById(R.id.tv_bg_top);
        tv_bg_top.setBackgroundResource(CUR_CAN_CHOOSE_LIST_PIC.get(position));
        tv_water_top.setText(catFood.getType() + ":" + catFood.getNumber() + "g");

        //middle层View
        LinearLayout linearLayout_middle = catFoodView.findViewById(R.id.linearLayout_middle);
        linearLayout_middle.setClickable(true);

        //bottom层View
        LinearLayout linearLayout_bottom = catFoodView.findViewById(R.id.linearLayout_bottom);
        linearLayout_bottom.setClickable(true);

        relativeLayout.addView(catFoodView);

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

        //top层点击事件
        linearLayout_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandView == null) {
                    openCurCheck(catFoodView, linearLayout_middle, linearLayout_bottom);
                } else {
                    if (expandView == catFoodView || expandView.equals(catFoodView)) {
                        openCurCheck(catFoodView, linearLayout_middle, linearLayout_bottom);
                    } else {
                        //如果已经展开，则先取消之前已经展开的打卡项
                        cancelExpandCheck();

                        //打开新点击的打卡项
                        openCurCheck(catFoodView, linearLayout_middle, linearLayout_bottom);
                    }
                }
            }
        });

        //middle层点击事件，即删除该打卡项
        linearLayout_middle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick:OnFail " + catFood.getType() + " " + catFood.getNumber());
                showCancelCheckDialog(position, catFood.getType(), catFoodView);
            }
        });

        //bottom层点击事件，即完成该打卡项
        linearLayout_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick:OnSuccess " + catFood.getType() + " " + catFood.getNumber());
                showFinishCheckDiialog(position, catFood.getType(), catFoodView);
            }
        });
    }

    private void showFinishCheckDiialog(int position, String checkItem, View catFoodView) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.finish_checkitem_dialog_layout, null);
        Button btn_ensure = view.findViewById(R.id.btn_ensure);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("MainActivity", "onClick: ensure");
                        Looper.prepare();
                        try {
                            boolean isFinishSuccess = HttpUtils.finishCheckItem(checkItem, MyApp.getCurTime());
                            Log.i(TAG, "run: isFinishSuccess:" + isFinishSuccess);
                            if (isFinishSuccess) {
                                Toast toast = Toast.makeText(MainActivity.this, "打卡成功！", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                checkViews.remove(position);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        relativeLayout.removeView(catFoodView);
                                        initGrowthRecord();
                                    }
                                });
                                dialog.dismiss();
                            } else {
                                Toast toast = Toast.makeText(MainActivity.this, "出现异常了！", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                dialog.dismiss();
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

    private void showCancelCheckDialog(int position, String checkItem, View catFoodView) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.cancel_checkitem_dialog_layout, null);
        Button btn_ensure = view.findViewById(R.id.btn_ensure);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("MainActivity", "onClick: ensure");
                        Looper.prepare();
                        try {
                            boolean isCancelSuccess = HttpUtils.cancelCheckItem(checkItem);
                            Log.i(TAG, "run: iisCancelSuccess:" + isCancelSuccess);
                            if (isCancelSuccess) {
                                Toast toast = Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                checkViews.remove(position);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        relativeLayout.removeView(catFoodView);
                                    }
                                });
                                dialog.dismiss();
                            } else {
                                Toast toast = Toast.makeText(MainActivity.this, "出现异常！", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                dialog.dismiss();
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

    private void openCurCheck(View catFoodView, LinearLayout linearLayout_middle, LinearLayout linearLayout_bottom) {
        ObjectAnimator objectAnimator = (ObjectAnimator) catFoodView.getTag();
        if (objectAnimator.isPaused()) {
            cancelMiddleViewAnimation(linearLayout_middle);
            cancelBottomViewAnimation(linearLayout_bottom);
            objectAnimator.resume();
            expandView = null;
        } else {
            objectAnimator.pause();
            setMiddleViewAnimation(linearLayout_middle);
            setBottomViewAnimation(linearLayout_bottom);
            expandMiddleLayout = linearLayout_middle;
            expandBottomLayout = linearLayout_bottom;
            expandView = catFoodView;
        }
    }

    private void cancelExpandCheck() {
        cancelMiddleViewAnimation(expandMiddleLayout);
        cancelBottomViewAnimation(expandBottomLayout);

        ObjectAnimator animator = (ObjectAnimator) expandView.getTag();
        animator.resume();
    }

    private void cancelBottomViewAnimation(LinearLayout linearLayout_bottom) {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(linearLayout_bottom, "rotation", -360f, 0f);

        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(linearLayout_bottom, "translationX",
                -100, 0);

        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(linearLayout_bottom, "translationY",
                -180, 0);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(linearLayout_bottom, "alpha", 1f, -1f);

        set.playTogether(rotationAnimator, translationXAnimator, translationYAnimator, alphaAnimator);
        set.setDuration(500);
        set.start();
    }

    private void cancelMiddleViewAnimation(LinearLayout linearLayout_middle) {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(linearLayout_middle, "rotation", 360f, 0f);

        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(linearLayout_middle, "translationX",
                100, 0);

        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(linearLayout_middle, "translationY",
                -180, 0);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(linearLayout_middle, "alpha", 1f, -1f);

        set.playTogether(rotationAnimator, translationXAnimator, translationYAnimator, alphaAnimator);
        set.setDuration(500);
        set.start();
    }

    private void setBottomViewAnimation(LinearLayout linearLayout_bottom) {

        linearLayout_bottom.setVisibility(linearLayout_bottom.getVisibility() == View.GONE ? View.VISIBLE : View.VISIBLE);

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(linearLayout_bottom, "rotation", 0f, -360f);

        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(linearLayout_bottom, "translationX",
                0, -100);

        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(linearLayout_bottom, "translationY",
                0, -180);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(linearLayout_bottom, "alpha", -1f, 1f);

        set.playTogether(rotationAnimator, translationXAnimator, translationYAnimator, alphaAnimator);
        set.setDuration(500);
        set.start();
    }

    private void setMiddleViewAnimation(LinearLayout linearLayout_middle) {

        linearLayout_middle.setVisibility(linearLayout_middle.getVisibility() == View.GONE ? View.VISIBLE : View.VISIBLE);

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(linearLayout_middle, "rotation", 0f, 360f);

        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(linearLayout_middle, "translationX",
                0, 100);

        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(linearLayout_middle, "translationY",
                0, -180);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(linearLayout_middle, "alpha", -1f, 1f);

        set.playTogether(rotationAnimator, translationXAnimator, translationYAnimator, alphaAnimator);
        set.setDuration(500);
        set.start();
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
