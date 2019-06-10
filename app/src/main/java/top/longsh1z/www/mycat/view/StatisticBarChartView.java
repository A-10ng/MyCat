package top.longsh1z.www.mycat.view;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public interface StatisticBarChartView extends IView {
    void getDataSuccess(ArrayList<BarEntry> barEntries);
    void getDataFailed();
}
