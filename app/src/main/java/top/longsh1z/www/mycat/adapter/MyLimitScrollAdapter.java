package top.longsh1z.www.mycat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.bean.MyCatWorldBean;
import top.longsh1z.www.mycat.customview.LimitScrollerView;
import top.longsh1z.www.mycat.ui.MainActivity;
import top.longsh1z.www.mycat.utils.MyApp;

public class MyLimitScrollAdapter implements LimitScrollerView.LimitScrollAdapter {

    private List<MyCatWorldBean> datas;

    public void setDatas(List<MyCatWorldBean> datas,LimitScrollerView limitScroll){
        this.datas = datas;
        //API:2、开始滚动
        limitScroll.startScroll();
    }
    @Override
    public int getCount() {
        return datas==null?0:datas.size();
    }

    @Override
    public View getView(int index) {
        View itemView = LayoutInflater.from(MyApp.getContext()).inflate(R.layout.limit_scroller_item, null, false);
        TextView tv_num = itemView.findViewById(R.id.tv_num);
        TextView tv_username = itemView.findViewById(R.id.tv_username);
        ImageView iv_gender = itemView.findViewById(R.id.iv_gender);
        TextView tv_catType = itemView.findViewById(R.id.tv_catType);
        TextView tv_catId = itemView.findViewById(R.id.tv_catId);
        TextView tv_catLevel = itemView.findViewById(R.id.tv_catLevel);

        //绑定数据
        MyCatWorldBean data = datas.get(index);
        itemView.setTag(data);
        tv_num.setText(data.getNum());
        tv_username.setText(data.getUsername());
        iv_gender.setImageResource(data.getGender());
        tv_catType.setText(data.getCatType());
        tv_catId.setText(data.getCatId());
        tv_catLevel.setText(data.getCatLevel()+"");
        return itemView;
    }
}
