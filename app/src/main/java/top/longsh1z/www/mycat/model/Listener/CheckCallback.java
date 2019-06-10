package top.longsh1z.www.mycat.model.Listener;

import java.util.List;

import top.longsh1z.www.mycat.bean.Check;

public interface CheckCallback {
    void OnSuccess(List<Check> checkList);
    void OnFailed();
}
