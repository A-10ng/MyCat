package top.longsh1z.www.mycat.model;

import com.github.mikephil.charting.data.BarEntry;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import top.longsh1z.www.mycat.bean.Check;
import top.longsh1z.www.mycat.model.Listener.BarEntryCallback;
import top.longsh1z.www.mycat.utils.HttpUtils;

public class StatisticBarChartModel implements IModel {
    private List<Check> checkList = new ArrayList<>();
    ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();

    public void getData(BarEntryCallback barEntryCallback) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkList = HttpUtils.getGrowthRecord();
                    barEntries = handleList(checkList);
                    barEntryCallback.OnSuccess(barEntries);
                } catch (Exception e) {
                    e.printStackTrace();
                    barEntryCallback.OnFailed();
                }
            }
        }).start();
    }

    private ArrayList<BarEntry> handleList(List<Check> checkList) {

        if (checkList == null) {
            //如果打卡记录为空，12个月份的Y都填充为
            for (int i = 1; i <= 12; i++) {
                barEntries.add(new BarEntry(i, 0));
            }
        } else {
            //如果有打卡记录，则统计当年每个月份的打卡次数
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                Calendar calendar = Calendar.getInstance();

                //获取当前年份
                int curyear = calendar.get(Calendar.YEAR);
                //int数组，用于存放每月的统计打卡数量
                int[] count = new int[13];
                for (int i = 1; i <= 12; i++) {
                    count[i] = 0;
                }
                for (Check check : checkList) {
                    String date = check.getDate();
                    calendar.setTime(simpleDateFormat.parse(date));
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    if (curyear == year) {
                        count[month]++;
                    }
                }
                //将统计完的数据写入barEntries中
                for(int i=1;i<=12;i++){
                    barEntries.add(new BarEntry(i, count[i]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return barEntries;
    }
}
