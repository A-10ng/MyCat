package top.longsh1z.www.mycat.presenter;

import java.lang.ref.WeakReference;

import top.longsh1z.www.mycat.model.IModel;
import top.longsh1z.www.mycat.view.IView;

public class IPresenter {
    protected IModel mIModel;
    protected WeakReference<IView> mViewRef;
}
