package top.longsh1z.www.mycat.model;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.logging.LogRecord;

import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.PointValue;
import top.longsh1z.www.mycat.bean.Check;
import top.longsh1z.www.mycat.model.Listener.ModelCallback;
import top.longsh1z.www.mycat.utils.CalendarUtils;
import top.longsh1z.www.mycat.utils.HttpUtils;

public class StatisticsLineChartModel implements IModel {
    private static final String TAG = "model";

    private List<Object> dataList = new ArrayList<>();
    private String[] date;
    private int[] value;
    private List<PointValue> mPointValues = new ArrayList<>();
    private List<AxisValue> mAxisXValues = new ArrayList<>();

    private List<Check> recordList = new ArrayList<>();

    //当前月份
    private String CurMonth = CalendarUtils.CurTimeInfor().get("month") + "";

    //当前月份所含的天数
    private int CurDaysOfMonth = (int) CalendarUtils.CurTimeInfor().get("daysAmount");

    private int left;
    private int right;

    public void getData(ModelCallback modelCallback) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    recordList = HttpUtils.getGrowthRecord();
                    dataList = handleList(recordList);
                    setLefRig();
                    modelCallback.OnSuccess(dataList,left,right);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    modelCallback.OnFailed();
                }
            }
        }).start();
    }

    private void setLefRig() {
        int date = (int) CalendarUtils.CurTimeInfor().get("CurDate");
        if (date<=4){
            left = 1;
            right = 7;
        }else if (date >4 && date<((List<AxisValue>)dataList.get(0)).size()-3){
            left = date-3;
            right = date+3;
        }else {
            left = ((List<AxisValue>)dataList.get(0)).size()-6;
            right = ((List<AxisValue>)dataList.get(0)).size();
        }
    }

    public List<Object> handleList(List<Check> checkList) throws ParseException {
        date = new String[CurDaysOfMonth+1];
        value = new int[CurDaysOfMonth+1];

        if (checkList == null) {
            setAxisValue(date);

            //填充折线图上的点全为0，因为没有打卡记录
            for (int i = 1; i < value.length; i++) {
                mPointValues.add(new PointValue(i, 0));
            }

            dataList.add(mAxisXValues);
            dataList.add(mPointValues);
            return dataList;
        } else {
            for (int i = 0; i < checkList.size(); i++) {
                //判断打卡记录是否为本月里的记录，是就放进value中，value的下标刚好对应具体的哪一天
                String monOfCheck = CalendarUtils.getCurMonthThroDate(checkList.get(i).getDate());
                int dateOfCheck = CalendarUtils.getCurDateThroDate(checkList.get(i).getDate());
                Log.i(TAG, "monOfCheck: "+monOfCheck);
                Log.i(TAG, "dateOfCheck: "+dateOfCheck);

                if (monOfCheck.equals(CurMonth) || monOfCheck == CurMonth) {
                    value[dateOfCheck]++;
                    Log.i(TAG, "ChoseddateOfCheck: "+dateOfCheck);
                }
            }
            setAxisValue(date);

            //填充折线图上的点
            for (int k = 1; k < value.length; k++) {
                mPointValues.add(new PointValue(k, value[k]));
            }

            dataList.add(mAxisXValues);
            dataList.add(mPointValues);
            Log.i(TAG, "handleList: " + dataList.size());
            return dataList;
        }
    }

    //填充X轴坐标
    private void setAxisValue(String[] date) {
        for (int i = 1; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(CurMonth + "月" + (i) + "号"));
        }
    }
}
