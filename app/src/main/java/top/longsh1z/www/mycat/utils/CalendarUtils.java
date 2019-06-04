package top.longsh1z.www.mycat.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CalendarUtils {

    //获取当前时间的工作日数量，实时年份，实时月份，自然日数量，当前月所包含的周数，当前月所包含的天数
    public static Map CurTimeInfor(){
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Map<Object, Integer> map = new HashMap<>();
        int workDays = 0;
        map.put("CurDate",calendar.get(Calendar.DATE));
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
            map.put("workDaysAmount", workDays);//工作日数量
            map.put("year", calendar.get(Calendar.YEAR));//实时年份
            map.put("month", calendar.get(Calendar.MONTH));//实时月份
            map.put("daysAmount", days);//自然日数量
            map.put("weeksAmount", calendar.getActualMaximum(Calendar.WEEK_OF_MONTH));//当前月所包含的周数
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String getCurMonthThroDate(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(simpleDateFormat.parse(date));
        return calendar1.get(Calendar.MONTH)+1+"";
    }

    public static int getCurDateThroDate(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(date));
        return calendar.get(Calendar.DATE);
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
