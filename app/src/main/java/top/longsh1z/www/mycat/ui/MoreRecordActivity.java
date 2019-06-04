package top.longsh1z.www.mycat.ui;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.adapter.CheckRecordAdapter;
import top.longsh1z.www.mycat.bean.CheckRecordBean;
import top.longsh1z.www.mycat.bean.Check;
import top.longsh1z.www.mycat.utils.HttpUtils;

public class MoreRecordActivity extends Activity {

    private static final String TAG = "MoreRecordActivity>>>";
    private RecyclerView mRecyclerView;
    //用于recyclerview的数据集合
    private List<Object> rv_dataList = new ArrayList<>();
    //用于上拉加载更多的recyclerview数据集合
    private List<Object> temp_dataList = new ArrayList<>();
    //打卡项记录的数据集合
    private List<Check> recordList = new ArrayList<>();
    //用于判断是否出现过的日期集合，避免重复绘制相同的日期
    private List<String> shownDateList = new ArrayList<>();

    //上拉加载更多
    private SmartRefreshLayout mSmartRefreshLayout;

    //读到recordList的位置
    private int recordList_position;

    private CheckRecordAdapter MyAdapter;

    private LinearLayout root_linearlayout;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_record);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        initRecyclerView();
                        Log.i(TAG, "onCreate: initRecyclerViews");
                        break;
                }
            }
        };

        initViews();
        Log.i(TAG, "onCreate: initViews");

        initRecyclerViewData();
        Log.i(TAG, "onCreate: initRecyclerViewsData");



        initSmartRefreshLayout();
        Log.i(TAG, "onCreate: initSmartFreshLayout");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rv_dataList.clear();
        temp_dataList.clear();
        recordList.clear();
        shownDateList.clear();
    }

    private void initViews() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mSmartRefreshLayout = findViewById(R.id.mSmartRefreshLayout);
        root_linearlayout = findViewById(R.id.root_layout);
    }

    private void initRecyclerViewData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    recordList = HttpUtils.getGrowthRecord();             //网络请求
                    if (recordList == null || recordList.size() == 0) {
                        Log.i(TAG, "run: recordlist = 0");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //如果没有打卡记录，则显示“暂无打卡记录哦！”
                                View view = LayoutInflater.from(MoreRecordActivity.this).inflate(R.layout.checkrecord_null_layout, null);
                                LinearLayout.LayoutParams ll_paras = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT);
                                view.setLayoutParams(ll_paras);

                                root_linearlayout.removeAllViews();
                                root_linearlayout.addView(view);
                            }
                        });
                    } else {
                        Log.i(TAG, "run: recordlist != 0");
                        AddDataToRecordList(0, 10, rv_dataList);           //上文说到的放数据进ArrayList
                        for (int i = 0; i < rv_dataList.size(); i++) {
                            Log.i(TAG, "run: recordlist != 0>>>>>"+rv_dataList.get(i));
                        }
                    }
                    handler.sendEmptyMessage(1);             //handler的sendMessage方法
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    Log.i(TAG, "run: 获取记录失败");
                }
            }
        }).start();

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        MyAdapter = new CheckRecordAdapter(rv_dataList);
        mRecyclerView.setAdapter(MyAdapter);
    }

    private void initSmartRefreshLayout() {
        mSmartRefreshLayout.setEnableRefresh(false);

        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                try {
                    List<Object> newList = getNewDataList(recordList_position + 1, recordList_position + 6);
                    if (newList == null || newList.equals(null)) {
                        mSmartRefreshLayout.finishLoadMore(10);
                    } else {
                        MyAdapter.addData(newList);
                        mSmartRefreshLayout.finishLoadMore(1000);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void AddDataToRecordList(int start, int end, List<Object> dataList) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));

        //当天时间信息
        Date curDate = new Date();
        Calendar curCalendar = Calendar.getInstance();
        curCalendar.setTime(curDate);
        int curYear = curCalendar.get(Calendar.YEAR);
        String curYeMonDa = curYear + "-" + (curCalendar.get(Calendar.MONTH) + 1) + "-" + (curCalendar.get(Calendar.DATE));

        //昨天时间信息
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.add(Calendar.DATE, -1);
        Date lastDate = lastCalendar.getTime();
        Log.i(TAG, "AddDataToRecordList: " + lastDate);
        lastCalendar.setTime(lastDate);
        int lastYear = lastCalendar.get(Calendar.YEAR);
        String lastYeMonDa = lastYear + "-" + (lastCalendar.get(Calendar.MONTH) + 1) + "-" + (lastCalendar.get(Calendar.DATE));

        //每条记录的时间信息
        Calendar recordCalendar = Calendar.getInstance();
        for (int i = start; i < end; i++) {
            if (i >= recordList.size()) {
                break;
            }
            Check curCheck = recordList.get(i);
            recordList_position = i;
            recordCalendar.setTime(simpleDateFormat.parse(curCheck.getDate()));
            int checkYear = recordCalendar.get(Calendar.YEAR);
            String checkYeMonDa = checkYear + "-" + (recordCalendar.get(Calendar.MONTH) + 1) + "-" + (recordCalendar.get(Calendar.DATE));
            String checkMonDa = (recordCalendar.get(Calendar.MONTH) + 1) + "-" + (recordCalendar.get(Calendar.DATE));
            if (!shownDateList.contains(checkYear) && checkYear != curYear) {
                dataList.add(checkYear + "");

                shownDateList.add(checkYear + "");
            }
            if (!shownDateList.contains(checkYeMonDa)) {
                if (curYeMonDa == checkYeMonDa || curYeMonDa.equals(checkYeMonDa)) {     //判断是否为今天
                    dataList.add("今天");

                    CheckRecordBean.ContentBean bean = new CheckRecordBean.ContentBean();
                    bean.setRecordContent("完成" + curCheck.getCheckItem() + "，喂养猫粮50g");
                    bean.setRecordTime(recordCalendar.get(Calendar.HOUR_OF_DAY) + ":" + recordCalendar.get(Calendar.MINUTE));
                    dataList.add(bean);

                    shownDateList.add(checkYeMonDa);
                } else if (lastYeMonDa == checkYeMonDa || lastYeMonDa.equals(checkYeMonDa)) {      //判断是否为昨天
                    dataList.add("昨天");

                    CheckRecordBean.ContentBean bean = new CheckRecordBean.ContentBean();
                    bean.setRecordContent("完成" + curCheck.getCheckItem() + "，喂养猫粮50g");
                    bean.setRecordTime(recordCalendar.get(Calendar.HOUR_OF_DAY) + ":" + recordCalendar.get(Calendar.MINUTE));
                    dataList.add(bean);

                    shownDateList.add(checkYeMonDa);
                } else {
                    dataList.add(checkMonDa);

                    CheckRecordBean.ContentBean bean = new CheckRecordBean.ContentBean();
                    bean.setRecordContent("完成" + curCheck.getCheckItem() + "，喂养猫粮50g");
                    bean.setRecordTime(recordCalendar.get(Calendar.HOUR_OF_DAY) + ":" + recordCalendar.get(Calendar.MINUTE));
                    dataList.add(bean);

                    shownDateList.add(checkYeMonDa);
                }
            } else {
                CheckRecordBean.ContentBean bean = new CheckRecordBean.ContentBean();
                bean.setRecordContent("完成" + curCheck.getCheckItem() + "，喂养猫粮50g");
                bean.setRecordTime(recordCalendar.get(Calendar.HOUR_OF_DAY) + ":" + recordCalendar.get(Calendar.MINUTE));
                dataList.add(bean);
            }
        }
    }

    private List<Object> getNewDataList(int start, int end) throws ParseException {
        temp_dataList.clear();
        AddDataToRecordList(start, end, temp_dataList);
        return temp_dataList;
    }
}
