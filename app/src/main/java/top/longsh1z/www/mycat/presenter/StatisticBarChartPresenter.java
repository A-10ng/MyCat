package top.longsh1z.www.mycat.presenter;

import com.github.mikephil.charting.data.BarEntry;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import top.longsh1z.www.mycat.model.Listener.BarEntryCallback;
import top.longsh1z.www.mycat.model.StatisticBarChartModel;
import top.longsh1z.www.mycat.view.StatisticBarChartView;

public class StatisticBarChartPresenter extends IPresenter {

    public StatisticBarChartPresenter(StatisticBarChartView mView){
        this.mIModel = new StatisticBarChartModel();
        this.mViewRef = new WeakReference<>(mView);
    }

    public void getData() throws InterruptedException{
        if (mIModel != null && mViewRef != null && mViewRef.get() != null){

            ((StatisticBarChartModel)mIModel).getData(new BarEntryCallback() {
                @Override
                public void OnSuccess(ArrayList<BarEntry> barEntries) {
                    if (mViewRef.get() != null){
                        ((StatisticBarChartView) mViewRef.get()).getDataSuccess(barEntries);
                    }
                }

                @Override
                public void OnFailed() {
                    if (mViewRef.get() != null){
                        ((StatisticBarChartView) mViewRef.get()).getDataFailed();
                    }
                }
            });
        }
    }
}
