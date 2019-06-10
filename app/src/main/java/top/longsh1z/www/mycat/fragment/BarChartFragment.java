package top.longsh1z.www.mycat.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.presenter.StatisticBarChartPresenter;
import top.longsh1z.www.mycat.view.StatisticBarChartView;
import top.longsh1z.www.mycat.viewholder.BackgroundDrawable;

public class BarChartFragment extends Fragment implements StatisticBarChartView {

    private View rootView;
    private BarChart barChart;
    private StatisticBarChartPresenter statisticBarChartPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.barchart_fragment, container, false);

        BackgroundDrawable drawable = BackgroundDrawable.builder()
                .left(30)//设置左侧斜切点的高度（取值范围是大于0，小于100）
                .right(40)//设置右侧侧斜切点的高度（取值范围是大于0，小于100）
                .topColor(Color.parseColor("#ead19a"))//设置上半部分的颜色（默认是白色）
               // .bottomColor(Color.parseColor("#76C4EB"))//设置下半部分的颜色（默认是白色）
                .build();//调用build进行创建。

//将这个drawable设置给View
        rootView.setBackground(drawable);

        initViews();
//        initData();

        try {
            initBarChartData();
            barChart.animateXY(3000, 3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void initViews() {
        barChart = rootView.findViewById(R.id.barChart);

        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(160);
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);


        //禁止触摸交互
        barChart.setTouchEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        //X轴显示数量
        xAxis.setLabelCount(12);
        xAxis.setLabelRotationAngle(-60);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int) value + "月";
            }
        });

        //设置Y轴的右轴不显示
        barChart.getAxisRight().setEnabled(false);


        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.animateY(2500);
        barChart.getLegend().setEnabled(false);
    }

    private void initBarChartData() throws InterruptedException {
        statisticBarChartPresenter = new StatisticBarChartPresenter(this);
        statisticBarChartPresenter.getData();
    }


    @Override
    public void getDataSuccess(ArrayList<BarEntry> barEntries) {
        initBarChart(barEntries);
    }

    private void initBarChart(ArrayList<BarEntry> barEntries) {
        BarDataSet set1;
        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(barEntries);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(barEntries, "打卡统计");
            //设置多彩 也可以单一颜色
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            //在柱状图上绘制值，如果值为零则不显示
            set1.setDrawValues(true);
            set1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    if (value == 0)
                        return "";
                    else
                        return "" + (int) value;
                }
            });

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
            barChart.setFitBars(true);
        }
        barChart.invalidate();
    }

    @Override
    public void getDataFailed() {

    }
}
