package top.longsh1z.www.mycat.ui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.EventBus;

import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.fragment.LineChartFragment;
import top.longsh1z.www.mycat.utils.ActivityCollector;

public class StatisticActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        ActivityCollector.addActivity(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout,new LineChartFragment());
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
