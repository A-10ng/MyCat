package top.longsh1z.www.mycat.utils;

import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CalendarUtils {

    private static Map CurTimeInfor(){
        Calendar calendar = Calendar.getInstance();
        Map<Object, Integer> map = new HashMap<>();
        int workDays = 0;
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        try {
            calendar.set(Calendar.DATE, 1);//从每月1号开始
            for (int i = 0; i < days; i++) {
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                if (!(day == Calendar.SUNDAY || day == Calendar.SATURDAY)) {
                    workDays++;
                }
                calendar.add(Calendar.DATE, 1);
            }
            map.put("workDaysAmount", workDays);//工作日
            map.put("year", calendar.get(Calendar.YEAR));//实时年份
            map.put("month", calendar.get(Calendar.MONTH));//实时月份
            map.put("daysAmount", days);//自然日
            map.put("weeksAmount", calendar.getActualMaximum(Calendar.WEEK_OF_MONTH));//周
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static int getCurYearsDays() {
        return (int) CurTimeInfor().get("daysAmount");
    }

    public static String getInternal(String befdate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Date curDate = new Date();
        long curDateMilSec = curDate.getTime();
        Date befDate = simpleDateFormat.parse(befdate);
        long befDateMilSec = befDate.getTime();
        long internal = curDateMilSec-befDateMilSec;
        long perMinute = 60*1000;
        int interMin = (int) (internal / perMinute);
        if (interMin < 60){
            if (interMin == 0){
                return "1分钟前";
            }else {
                return interMin+"分钟前";
            }
        }else if (interMin >= 60 && interMin <1440){
            int interHour = interMin / 60;
            return interHour+"小时前";
        }else if (interMin >= 1440 && interMin < 43200){
            int interDay = interMin / 1440;
            return interDay+"天前";
        }else if (interMin >= 43200 && interMin < 518400){
            int interMon = interMin / 43200;
            return interMon+"个月前";
        }else{
            int interYear = interMin / 518400;
            return interYear+"年前";
        }
    }
}
