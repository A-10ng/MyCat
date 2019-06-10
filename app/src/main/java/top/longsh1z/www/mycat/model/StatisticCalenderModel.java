package top.longsh1z.www.mycat.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


import top.longsh1z.www.mycat.bean.Check;
import top.longsh1z.www.mycat.model.Listener.CheckCallback;
import top.longsh1z.www.mycat.utils.HttpUtils;

public class StatisticCalenderModel implements IModel {
    private static final String TAG = "Calendarmodel";

    private List<Check> checkList = new ArrayList<>();


    public void getData(CheckCallback checkCallback) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkList = HttpUtils.getGrowthRecord();
//                    for(Check check:checkList)
//                        Log.i(TAG, "Id: "+check.getCheckId()+" "+check.getDate());

                    checkCallback.OnSuccess(checkList);
                } catch (Exception e) {
                    e.printStackTrace();
                    checkCallback.OnFailed();
                }
            }
        }).start();
    }
}
