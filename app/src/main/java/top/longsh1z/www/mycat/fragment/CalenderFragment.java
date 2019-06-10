package top.longsh1z.www.mycat.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import top.longsh1z.www.mycat.R;
import top.longsh1z.www.mycat.bean.Check;
import top.longsh1z.www.mycat.presenter.StatisticCalenderPresenter;
import top.longsh1z.www.mycat.view.StatisticCalenderView;
import top.longsh1z.www.mycat.viewholder.BackgroundDrawable;

public class CalenderFragment extends Fragment implements StatisticCalenderView,
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener{

    private static final String TAG = "calendar";

    private View rootView;
    private StatisticCalenderPresenter statisticCalenderPresenter;
    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.calender, container, false);

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
            initCalenderViewData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void initViews() {

        mTextMonthDay = rootView.findViewById(R.id.tv_month_day);
        mTextYear = rootView.findViewById(R.id.tv_year);
        mTextLunar = rootView.findViewById(R.id.tv_lunar);
        mRelativeTool = rootView.findViewById(R.id.rl_tool);
        mCalendarView = rootView.findViewById(R.id.calendarView);
        mTextCurrentDay = rootView.findViewById(R.id.tv_current_day);

        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        rootView.findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });

        mCalendarLayout = rootView.findViewById(R.id.calendarLayout);

        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);

        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
    }

    private void initCalenderViewData() throws InterruptedException {

        statisticCalenderPresenter = new StatisticCalenderPresenter(this);
        statisticCalenderPresenter.getData();
    }

    @Override
    public void getDataSuccess(List<Check> checkList) {
        initCalender(checkList);//初始化
    }

    private void initCalender(List<Check> checkList) {
        //根据每天的打卡数量设置颜色
        int[] color=new int[5];
        color[0]=0xFF40db25;//打卡1次
        color[1]=0xFFe69138;//打卡2次
        color[2]=0xFFdf1356;//打卡3次
        color[3]=0xFFbc13f0;//打卡4次
        color[4]=0xFF13acf0;//打卡5次

        //定义hashmap，用于存放获取的数据
        Map<String, Calendar> map = new HashMap<>();
        //第0天
        int tmpyear=0;
        int tmpmonth=0;
        int tmpday=0;
        //统计每天打卡次数
        int count=0;
        //记录循环次数
        int i=0;
        //文本
        String text="";
        //遍历List中的Check对象
        for(Check check:checkList){

            //获取对象的内容
            String date=check.getDate();
            String checkItem=check.getCheckItem();



            //对时间进行转换，分别获取年月日
            int year,month,day;
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.setTime(simpleDateFormat.parse(date));
                year=calendar.get(java.util.Calendar.YEAR);
                month=calendar.get(java.util.Calendar.MONTH)+1;
                day=calendar.get(java.util.Calendar.DATE);

                //Log.i(TAG, "day: "+check.getCheckId()+" "+month+"-"+day);

//                Log.i(TAG, "size: "+i+" "+(checkList.size()-1));
                if(i==(checkList.size()-1)){
                    if(count>=4)
                        count=4;
                    map.put(getSchemeCalendar(year, month, day, color[count], text).toString(),
                            getSchemeCalendar(year, month, day, color[count], text));
//                    Log.i(TAG, "ifId: "+month+"-"+day+" "+count);
                }else if((tmpday==day&&tmpmonth==month&&tmpyear==year)||tmpday==0){
                    text=checkItem+",";
                    count++;
                }else{
                    if(count>=4)
                        count=4;
                    map.put(getSchemeCalendar(tmpyear, tmpmonth, tmpday, color[count], text).toString(),
                            getSchemeCalendar(tmpyear, tmpmonth, tmpday, color[count], text));
//                    Log.i(TAG, "Id: "+tmpmonth+"-"+tmpday+" "+count);
                    count=0;
                }
                tmpday=day;
                tmpyear=year;
                tmpmonth=month;

            } catch (ParseException e) {
                e.printStackTrace();
            }
            i++;
        }
        mCalendarView.setSchemeDate(map);

    }

//测试
    protected void initData() {
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();



        Map<String, Calendar> map = new HashMap<>();
        map.put(getSchemeCalendar(year, month, 3, 0xFF40db25, "假").toString(),
                getSchemeCalendar(year, month, 3, 0xFF40db25, "假"));
        map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "事").toString(),
                getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
        map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "议").toString(),
                getSchemeCalendar(year, month, 9, 0xFFdf1356, "议"));
        map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 13, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 14, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 14, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 15, 0xFFaacc44, "假").toString(),
                getSchemeCalendar(year, month, 15, 0xFFaacc44, "假"));
        map.put(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记").toString(),
                getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记"));
        map.put(getSchemeCalendar(year, month, 25, 0xFF13acf0, "假").toString(),
                getSchemeCalendar(year, month, 25, 0xFF13acf0, "假"));
        map.put(getSchemeCalendar(year, month, 27, 0xFF13acf0, "多").toString(),
                getSchemeCalendar(year, month, 27, 0xFF13acf0, "多"));
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);
    }

    @Override
    public void getDataFailed() {
        Toast toast = Toast.makeText(getActivity(), "获取数据失败了...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
    }

    @Override
    public void onYearChange(int year) {

    }
}
