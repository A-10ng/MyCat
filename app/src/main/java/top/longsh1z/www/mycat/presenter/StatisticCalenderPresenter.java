package top.longsh1z.www.mycat.presenter;

import java.lang.ref.WeakReference;
import java.util.List;

import top.longsh1z.www.mycat.bean.Check;
import top.longsh1z.www.mycat.model.Listener.CheckCallback;
import top.longsh1z.www.mycat.model.StatisticCalenderModel;
import top.longsh1z.www.mycat.view.StatisticCalenderView;

public class StatisticCalenderPresenter extends IPresenter{
    private static final String TAG = "Presenter";

    public StatisticCalenderPresenter(StatisticCalenderView mView){
        this.mIModel = new StatisticCalenderModel();
        this.mViewRef = new WeakReference<>(mView);
    }


    public void getData() throws InterruptedException {

        if (mIModel != null && mViewRef != null && mViewRef.get() != null){

            ((StatisticCalenderModel)mIModel).getData(new CheckCallback() {
                @Override
                public void OnSuccess(List<Check> checkList) {
                    if (mViewRef.get() != null){
                        ((StatisticCalenderView) mViewRef.get()).getDataSuccess(checkList);
                    }
                }

                @Override
                public void OnFailed() {
                    if (mViewRef.get() != null){
                        ((StatisticCalenderView) mViewRef.get()).getDataFailed();
                    }
                }
            });
        }
    }
}
