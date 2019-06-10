package top.longsh1z.www.mycat.view;

import java.util.List;

import top.longsh1z.www.mycat.bean.Check;

public interface StatisticCalenderView extends IView {
    void getDataSuccess(List<Check> checkList);
    void getDataFailed();
}
