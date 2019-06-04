package top.longsh1z.www.mycat.view;

import java.util.List;

public interface StatisticLineChartView extends IView {
    void getDataSuccess(List<Object> dataList,int left,int right);
    void getDataFailed();
}
