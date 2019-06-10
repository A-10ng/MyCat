package top.longsh1z.www.mycat.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;


import java.util.ArrayList;
import java.util.List;

import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.fragment.BarChartFragment;
import top.longsh1z.www.mycat.fragment.CalenderFragment;
import top.longsh1z.www.mycat.fragment.LineChartFragment;
import top.longsh1z.www.mycat.utils.ActivityCollector;
import top.longsh1z.www.mycat.view.HorizontalViewPager;

public class StatisticActivity extends AppCompatActivity {

    //    private FrameLayout frameLayout;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_statistic);
//
////        FragmentManager fragmentManager = getSupportFragmentManager();
////        FragmentTransaction transaction = fragmentManager.beginTransaction();
////        transaction.replace(R.id.framelayout,new LineChartFragment());
////        transaction.commit();
//    }

    private TabLayout tabLayout;
//    private ViewPager viewPager;

    private HorizontalViewPager viewPager;

    private List<Fragment> list;
    private MyAdapter adapter;
    private String[] titles = {"打卡情况", "近一个月", "近一年"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        //实例化
        viewPager = (HorizontalViewPager) findViewById(R.id.viewpager);

//        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        //页面，数据源
        list = new ArrayList<>();
        list.add(new CalenderFragment());
        list.add(new LineChartFragment());
        list.add(new BarChartFragment());
        //ViewPager的适配器
        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        //绑定
        tabLayout.setupWithViewPager(viewPager);
    }


    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        //重写这个方法，将设置每个Tab的标题
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
