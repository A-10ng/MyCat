package top.longsh1z.www.mycat.presenter;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import top.longsh1z.www.mycat.model.Listener.ModelCallback;
import top.longsh1z.www.mycat.model.StatisticsLineChartModel;
import top.longsh1z.www.mycat.view.StatisticLineChartView;

public class StatisticLineChartPresenter extends IPresenter {
    private static final String TAG = "Presenter";

    public StatisticLineChartPresenter(StatisticLineChartView mView){
        this.mIModel = new StatisticsLineChartModel();
        this.mViewRef = new WeakReference<>(mView);
    }


    public void getData() throws InterruptedException {

        if (mIModel != null && mViewRef != null && mViewRef.get() != null){

            ((StatisticsLineChartModel)mIModel).getData(new ModelCallback() {
                @Override
                public void OnSuccess(List<Object> dataList,int left,int right) {
                    if (mViewRef.get() != null){
                        ((StatisticLineChartView) mViewRef.get()).getDataSuccess(dataList,left,right);
                    }
                }

                @Override
                public void OnFailed() {
                    if (mViewRef.get() != null){
                        ((StatisticLineChartView) mViewRef.get()).getDataFailed();
                    }
                }
            });
        }
    }
}
