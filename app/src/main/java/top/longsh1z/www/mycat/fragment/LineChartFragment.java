package top.longsh1z.www.mycat.fragment;

import android.app.Instrumentation;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.customview.ChartView;
import top.longsh1z.www.mycat.presenter.StatisticLineChartPresenter;
import top.longsh1z.www.mycat.ui.MainActivity;
import top.longsh1z.www.mycat.view.StatisticLineChartView;

public class LineChartFragment extends Fragment implements StatisticLineChartView {

    private View rootView;
    private StatisticLineChartPresenter statisticLineChartPresenter;
    private LineChartView chartView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.linechart_fragment, container, false);

        initViews();

        try {
            initChartViewData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void initViews() {
        chartView = rootView.findViewById(R.id.chartView);
    }

    private void initChartViewData() throws InterruptedException {
        statisticLineChartPresenter = new StatisticLineChartPresenter(this);
        statisticLineChartPresenter.getData();
    }

    @Override
    public void getDataSuccess(List<Object> dataList,int left,int right) {
        initLineChart(dataList,left,right);//初始化
    }

    private void initLineChart(List<Object> dataList,int left,int right) {
        Line line = new Line((List<PointValue>)dataList.get(1)).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        //line.setHasLabels(true);//曲线的数据坐标是否加上备注
        //line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //X轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(7); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues((List<AxisValue>) dataList.get(0));  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部

        //Y轴
        Axis axisY = new Axis();
        axisY.setMaxLabelChars(6);//max label length, for example 60
        List<AxisValue> values = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            AxisValue value = new AxisValue(i);
            String label = i + "";
            value.setLabel(label);
            values.add(value);
        }
        axisY.setTextColor(Color.WHITE);
        axisY.setTextSize(10);
        axisY.setValues(values);
        data.setAxisYLeft(axisY);


        //设置行为属性，支持缩放、滑动以及平移
        chartView.setInteractive(true);
        chartView.setZoomEnabled(false);
        chartView.setLineChartData(data);
        chartView.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        //固定Y轴
        Viewport v1 = new Viewport(chartView.getMaximumViewport());
        //v.left = ((List<AxisValue>) dataList.get(0)).size()-8;
        //v.right = ((List<AxisValue>) dataList.get(0)).size()-1;
        v1.bottom = 0;
        v1.top = 5;
        chartView.setMaximumViewport(v1);

        Viewport v = new Viewport(chartView.getMaximumViewport());
        //v.left = ((List<AxisValue>) dataList.get(0)).size()-8;
        //v.right = ((List<AxisValue>) dataList.get(0)).size()-1;
        v.left = left;
        v.right = right;
        chartView.setCurrentViewport(v);
    }

    @Override
    public void getDataFailed() {
        Toast toast = Toast.makeText(getActivity(), "获取数据失败了...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
