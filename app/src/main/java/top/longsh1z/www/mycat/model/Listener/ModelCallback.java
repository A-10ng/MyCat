package top.longsh1z.www.mycat.model.Listener;


import java.util.List;

public interface ModelCallback {
    void OnSuccess(List<Object> dataList,int left,int right);
    void OnFailed();
}
